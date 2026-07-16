#!/usr/bin/env python3
"""Deep WayToAGI synchronization for Feishu virtualized documents.

Feishu may remove off-screen document blocks from the DOM. This script captures
text at multiple scroll positions and merges the snapshots before writing the
same cache used by the daily report.
"""

from __future__ import annotations

import asyncio
import math
import os
import sys
from typing import Any

from playwright.async_api import async_playwright

from sync_waytoagi import (
    DEFAULT_URL,
    MIN_CONTENT_CHARS,
    USER_AGENT,
    create_context,
    dismiss_overlays,
    extract_best_text,
    is_usable,
    load_storage_state,
    normalize_text,
    synchronize,
    write_success,
)


def merge_snapshots(snapshots: list[str]) -> str:
    """Merge virtualized-page snapshots while removing exact duplicate lines."""
    seen: set[str] = set()
    merged: list[str] = []
    for snapshot in snapshots:
        for raw_line in normalize_text(snapshot).splitlines():
            line = raw_line.strip()
            if not line or line in seen:
                continue
            seen.add(line)
            merged.append(line)
    return "\n".join(merged)


async def mark_largest_scroller(page: Any) -> int:
    return int(
        await page.evaluate(
            """
            () => {
              document.querySelectorAll('[data-waytoagi-scroller]').forEach(
                el => el.removeAttribute('data-waytoagi-scroller')
              );
              const candidates = [
                document.scrollingElement,
                ...document.querySelectorAll('main, article, [role="main"], div')
              ].filter(Boolean);
              let best = document.scrollingElement;
              let bestRange = best ? best.scrollHeight - best.clientHeight : 0;
              for (const element of candidates) {
                const range = element.scrollHeight - element.clientHeight;
                if (range > bestRange && element.clientHeight > 300) {
                  best = element;
                  bestRange = range;
                }
              }
              if (best) best.setAttribute('data-waytoagi-scroller', 'true');
              return Math.max(0, bestRange);
            }
            """
        )
    )


async def collect_links(page: Any) -> list[dict[str, str]]:
    try:
        return await page.locator("a[href]").evaluate_all(
            "els => els.map(a => ({text: (a.innerText || '').trim(), href: a.href}))"
        )
    except Exception:
        return []


async def deep_fetch(url: str) -> dict[str, Any]:
    state = load_storage_state()
    async with async_playwright() as playwright:
        browser = await playwright.chromium.launch(
            headless=True,
            args=[
                "--disable-blink-features=AutomationControlled",
                "--disable-dev-shm-usage",
                "--no-sandbox",
            ],
        )
        context = await create_context(browser, state)
        page = await context.new_page()
        try:
            response = await page.goto(url, wait_until="domcontentloaded", timeout=90_000)
            await page.wait_for_timeout(10_000)
            await dismiss_overlays(page)

            snapshots: list[str] = []
            all_links: list[dict[str, str]] = []
            initial = await extract_best_text(page)
            if initial:
                snapshots.append(initial)
            all_links.extend(await collect_links(page))

            max_scroll = await mark_largest_scroller(page)
            step = 750
            positions = list(range(0, max_scroll + step, step))
            # Keep the workflow bounded while still sampling long documents.
            if len(positions) > 80:
                positions = [
                    round(index * max_scroll / 79)
                    for index in range(80)
                ]

            scroller = page.locator("[data-waytoagi-scroller='true']")
            for position in positions:
                if await scroller.count():
                    await scroller.first.evaluate("(el, y) => { el.scrollTop = y; }", position)
                else:
                    await page.evaluate("y => window.scrollTo(0, y)", position)
                await page.wait_for_timeout(550)
                snapshot = await extract_best_text(page)
                if snapshot:
                    snapshots.append(snapshot)
                all_links.extend(await collect_links(page))

            text = merge_snapshots(snapshots)
            return {
                "method": "playwright-virtualized-scroll",
                "text": text,
                "title": (await page.title()).strip(),
                "final_url": page.url,
                "response_status": response.status if response else None,
                "links": all_links,
                "html": await page.content(),
                "scroll_range": max_scroll,
                "snapshot_count": len(snapshots),
            }
        finally:
            await context.close()
            await browser.close()


async def main_async() -> int:
    url = os.getenv("WAYTOAGI_URL", DEFAULT_URL)
    try:
        result = await deep_fetch(url)
        text = str(result.get("text", ""))
        if is_usable(text, str(result.get("final_url", ""))) and len(text) >= MIN_CONTENT_CHARS:
            write_success(result, url)
            print(
                "WayToAGI deep synchronization succeeded: "
                f"{len(text)} chars, {result.get('snapshot_count')} snapshots, "
                f"scroll range {result.get('scroll_range')}"
            )
            return 0
        print("Deep extraction was insufficient; using standard fallback.", file=sys.stderr)
    except Exception as exc:  # noqa: BLE001
        print(f"Deep extraction failed: {type(exc).__name__}: {exc}", file=sys.stderr)

    return await synchronize(url)


if __name__ == "__main__":
    raise SystemExit(asyncio.run(main_async()))
