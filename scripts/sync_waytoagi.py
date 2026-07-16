#!/usr/bin/env python3
"""Synchronize a public WayToAGI Feishu wiki page into a Markdown cache.

The script first uses a real Chromium browser through Playwright because Feishu
public wiki pages may redirect repeatedly for non-browser HTTP clients. If the
browser result is unavailable or too short, it falls back to Jina Reader.

Optional authentication:
- Local interactive login: python scripts/sync_waytoagi.py --login
- CI: store the generated JSON as FEISHU_STORAGE_STATE_JSON.
"""

from __future__ import annotations

import argparse
import asyncio
import hashlib
import json
import os
import re
import sys
import urllib.request
from datetime import datetime
from pathlib import Path
from typing import Any
from zoneinfo import ZoneInfo

from playwright.async_api import Browser, BrowserContext, Page, async_playwright

DEFAULT_URL = "https://waytoagi.feishu.cn/wiki/QPe5w5g7UisbEkkow8XcDmOpn8e"
OUTPUT_DIR = Path("data/sources/waytoagi")
LATEST_PATH = OUTPUT_DIR / "latest.md"
STATUS_PATH = OUTPUT_DIR / "status.json"
DEBUG_HTML_PATH = OUTPUT_DIR / "last-error.html"
LOCAL_STATE_PATH = Path(".secrets/feishu-storage-state.json")
MIN_CONTENT_CHARS = 800

USER_AGENT = (
    "Mozilla/5.0 (Windows NT 10.0; Win64; x64) "
    "AppleWebKit/537.36 (KHTML, like Gecko) "
    "Chrome/150.0.0.0 Safari/537.36"
)

BLOCKED_MARKERS = (
    "登录飞书",
    "扫码登录",
    "安全验证",
    "访问受限",
    "页面不存在",
    "redirect limit",
    "too many redirects",
)


def now_shanghai() -> datetime:
    return datetime.now(ZoneInfo("Asia/Shanghai"))


def normalize_text(text: str) -> str:
    text = text.replace("\u200b", "").replace("\ufeff", "")
    text = re.sub(r"[ \t]+\n", "\n", text)
    text = re.sub(r"\n{3,}", "\n\n", text)
    return text.strip()


def is_usable(text: str, final_url: str = "") -> bool:
    lowered = f"{final_url}\n{text[:3000]}".lower()
    if len(text.strip()) < MIN_CONTENT_CHARS:
        return False
    return not any(marker.lower() in lowered for marker in BLOCKED_MARKERS)


def load_storage_state() -> dict[str, Any] | str | None:
    raw = os.getenv("FEISHU_STORAGE_STATE_JSON", "").strip()
    if raw:
        try:
            return json.loads(raw)
        except json.JSONDecodeError as exc:
            raise RuntimeError("FEISHU_STORAGE_STATE_JSON is not valid JSON") from exc
    if LOCAL_STATE_PATH.exists():
        return str(LOCAL_STATE_PATH)
    return None


async def create_context(browser: Browser, state: dict[str, Any] | str | None) -> BrowserContext:
    kwargs: dict[str, Any] = {
        "locale": "zh-CN",
        "timezone_id": "Asia/Shanghai",
        "user_agent": USER_AGENT,
        "viewport": {"width": 1440, "height": 1000},
        "extra_http_headers": {
            "Accept-Language": "zh-CN,zh;q=0.9,en;q=0.8",
            "Upgrade-Insecure-Requests": "1",
        },
    }
    if state is not None:
        kwargs["storage_state"] = state
    return await browser.new_context(**kwargs)


async def dismiss_overlays(page: Page) -> None:
    labels = ("我知道了", "继续访问", "在浏览器中打开", "关闭", "取消")
    for label in labels:
        try:
            locator = page.get_by_text(label, exact=True)
            if await locator.count() and await locator.first.is_visible():
                await locator.first.click(timeout=1500)
        except Exception:
            continue


async def extract_best_text(page: Page) -> str:
    selectors = (
        "article",
        "main",
        "[role='main']",
        ".docx-page-content",
        ".suite-web-editor",
        ".wiki-content",
        "body",
    )
    candidates: list[str] = []
    for selector in selectors:
        try:
            locator = page.locator(selector)
            count = min(await locator.count(), 5)
            for index in range(count):
                text = normalize_text(await locator.nth(index).inner_text(timeout=5000))
                if text:
                    candidates.append(text)
        except Exception:
            continue
    return max(candidates, key=len, default="")


async def browser_fetch(url: str, *, headed: bool = False) -> dict[str, Any]:
    state = load_storage_state()
    async with async_playwright() as playwright:
        browser = await playwright.chromium.launch(
            headless=not headed,
            args=[
                "--disable-blink-features=AutomationControlled",
                "--disable-dev-shm-usage",
                "--no-sandbox",
            ],
        )
        context = await create_context(browser, state)
        page = await context.new_page()
        response_status: int | None = None
        try:
            response = await page.goto(url, wait_until="domcontentloaded", timeout=90_000)
            response_status = response.status if response else None
            await page.wait_for_timeout(10_000)
            await dismiss_overlays(page)

            # Feishu documents load content lazily. Scroll several times to force
            # the browser to materialize more blocks before extraction.
            for _ in range(10):
                await page.mouse.wheel(0, 1800)
                await page.wait_for_timeout(700)
            await page.evaluate("window.scrollTo(0, 0)")
            await page.wait_for_timeout(1000)

            text = await extract_best_text(page)
            links = await page.locator("a[href]").evaluate_all(
                "els => els.map(a => ({text: (a.innerText || '').trim(), href: a.href}))"
            )
            return {
                "method": "playwright",
                "text": text,
                "title": (await page.title()).strip(),
                "final_url": page.url,
                "response_status": response_status,
                "links": links,
                "html": await page.content(),
            }
        finally:
            await context.close()
            await browser.close()


def jina_reader_fetch(url: str) -> dict[str, Any]:
    reader_url = "https://r.jina.ai/http://" + re.sub(r"^https?://", "", url)
    request = urllib.request.Request(
        reader_url,
        headers={
            "User-Agent": USER_AGENT,
            "Accept": "text/plain,text/markdown;q=0.9,*/*;q=0.8",
        },
    )
    with urllib.request.urlopen(request, timeout=90) as response:  # noqa: S310
        payload = response.read().decode("utf-8", errors="replace")
    text = normalize_text(payload)
    title_match = re.search(r"^Title:\s*(.+)$", text, re.MULTILINE)
    return {
        "method": "jina-reader",
        "text": text,
        "title": title_match.group(1).strip() if title_match else "WayToAGI",
        "final_url": reader_url,
        "response_status": 200,
        "links": [],
        "html": "",
    }


def format_links(items: list[dict[str, Any]]) -> str:
    seen: set[str] = set()
    lines: list[str] = []
    for item in items:
        href = str(item.get("href", "")).strip()
        label = normalize_text(str(item.get("text", "")))
        if not href.startswith(("http://", "https://")) or href in seen:
            continue
        seen.add(href)
        label = label[:120] or href
        label = label.replace("[", "\\[").replace("]", "\\]")
        lines.append(f"- [{label}]({href})")
        if len(lines) >= 300:
            break
    return "\n".join(lines)


def write_success(result: dict[str, Any], source_url: str) -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    fetched_at = now_shanghai().isoformat(timespec="seconds")
    text = normalize_text(str(result["text"]))
    digest = hashlib.sha256(text.encode("utf-8")).hexdigest()
    title = normalize_text(str(result.get("title") or "WayToAGI"))
    final_url = str(result.get("final_url") or source_url)
    links = format_links(list(result.get("links") or []))

    markdown = "\n".join(
        [
            "---",
            'title: "WayToAGI Source Cache"',
            f'fetched_at: "{fetched_at}"',
            f'source_url: {json.dumps(source_url, ensure_ascii=False)}',
            f'final_url: {json.dumps(final_url, ensure_ascii=False)}',
            f'retrieval_method: "{result["method"]}"',
            f'content_sha256: "{digest}"',
            f'page_title: {json.dumps(title, ensure_ascii=False)}',
            "status: \"success\"",
            "---",
            "",
            f"# {title or 'WayToAGI'}",
            "",
            text,
            "",
            "## 页面链接",
            "",
            links or "- 页面正文未提取到独立外链。",
            "",
        ]
    )
    LATEST_PATH.write_text(markdown, encoding="utf-8")
    STATUS_PATH.write_text(
        json.dumps(
            {
                "status": "success",
                "fetched_at": fetched_at,
                "source_url": source_url,
                "final_url": final_url,
                "method": result["method"],
                "response_status": result.get("response_status"),
                "content_chars": len(text),
                "content_sha256": digest,
            },
            ensure_ascii=False,
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )
    DEBUG_HTML_PATH.unlink(missing_ok=True)


def write_failure(source_url: str, errors: list[str], html: str = "") -> None:
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    fetched_at = now_shanghai().isoformat(timespec="seconds")
    STATUS_PATH.write_text(
        json.dumps(
            {
                "status": "failed",
                "fetched_at": fetched_at,
                "source_url": source_url,
                "errors": errors,
                "hint": (
                    "If the page requires authentication, run "
                    "`python scripts/sync_waytoagi.py --login` locally and save "
                    "the generated storage state JSON as the repository secret "
                    "FEISHU_STORAGE_STATE_JSON."
                ),
            },
            ensure_ascii=False,
            indent=2,
        )
        + "\n",
        encoding="utf-8",
    )
    if html:
        DEBUG_HTML_PATH.write_text(html[:300_000], encoding="utf-8")


async def interactive_login(url: str) -> int:
    LOCAL_STATE_PATH.parent.mkdir(parents=True, exist_ok=True)
    async with async_playwright() as playwright:
        browser = await playwright.chromium.launch(headless=False)
        context = await browser.new_context(locale="zh-CN", user_agent=USER_AGENT)
        page = await context.new_page()
        await page.goto(url, wait_until="domcontentloaded", timeout=90_000)
        print("Complete Feishu login in the opened browser, then press Enter here.")
        await asyncio.to_thread(input)
        await context.storage_state(path=str(LOCAL_STATE_PATH))
        await context.close()
        await browser.close()
    print(f"Saved storage state to {LOCAL_STATE_PATH}")
    return 0


async def synchronize(url: str) -> int:
    errors: list[str] = []
    browser_result: dict[str, Any] | None = None

    try:
        browser_result = await browser_fetch(url)
        if is_usable(str(browser_result["text"]), str(browser_result["final_url"])):
            write_success(browser_result, url)
            print(f"WayToAGI synchronized with Playwright: {len(browser_result['text'])} chars")
            return 0
        errors.append(
            "Playwright returned insufficient or blocked content "
            f"(final_url={browser_result.get('final_url')}, "
            f"chars={len(str(browser_result.get('text', '')))})"
        )
    except Exception as exc:  # noqa: BLE001
        errors.append(f"Playwright failed: {type(exc).__name__}: {exc}")

    try:
        reader_result = await asyncio.to_thread(jina_reader_fetch, url)
        if is_usable(str(reader_result["text"]), str(reader_result["final_url"])):
            write_success(reader_result, url)
            print(f"WayToAGI synchronized with Jina Reader: {len(reader_result['text'])} chars")
            return 0
        errors.append(
            "Jina Reader returned insufficient or blocked content "
            f"(chars={len(str(reader_result.get('text', '')))})"
        )
    except Exception as exc:  # noqa: BLE001
        errors.append(f"Jina Reader failed: {type(exc).__name__}: {exc}")

    write_failure(url, errors, str(browser_result.get("html", "")) if browser_result else "")
    print("\n".join(errors), file=sys.stderr)
    return 1


def parse_args() -> argparse.Namespace:
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument("--url", default=os.getenv("WAYTOAGI_URL", DEFAULT_URL))
    parser.add_argument("--login", action="store_true", help="Open a browser and save Feishu login state")
    return parser.parse_args()


def main() -> int:
    args = parse_args()
    if args.login:
        return asyncio.run(interactive_login(args.url))
    return asyncio.run(synchronize(args.url))


if __name__ == "__main__":
    raise SystemExit(main())
