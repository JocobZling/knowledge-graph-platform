# Git Operations Notes

Last updated: 2026-07-03

This note records the Git pull/push issues seen in this workspace and the local workflow to avoid repeating them.

## Observed Problems

- `git fetch` and `git pull` failed inside Codex without escalation:
  - Error: `cannot open '.git/FETCH_HEAD': Permission denied`
  - Cause: the Codex sandbox could read `.git`, but normal commands could not write Git metadata such as `FETCH_HEAD` or `index.lock`.
  - Fix in Codex: rerun the same Git command with approval/escalation.
  - Fix in a normal local terminal: run Git from the repository root with normal user permissions.
- `git add` failed inside Codex without escalation:
  - Error: `Unable to create '.git/index.lock': Permission denied`
  - Cause: the same sandbox write restriction on `.git`.
  - Fix in Codex: rerun the exact `git add` command with approval/escalation.
- `git push --dry-run origin main` returned `Everything up-to-date` while files were modified locally.
  - Cause: local edits had not been committed yet. Git only pushes commits, not unstaged or staged working tree changes.
  - Fix: review `git diff`, stage the intended files, commit, then push.
- `npm run build` failed in PowerShell because `npm.ps1` is blocked by the execution policy.
  - Error: `cannot load file ... npm.ps1 because running scripts is disabled`
  - Fix: use `npm.cmd run build` from PowerShell.

## Safe Daily Commands

From the repository root:

```cmd
scripts\git-pull.cmd
```

This script checks the worktree is clean, fetches `origin`, and runs:

```cmd
git pull --ff-only
```

Use this for pushing committed local work:

```cmd
scripts\git-push.cmd
```

This script checks the worktree is clean, blocks tracked `.env` files, fetches `origin`, refuses to push if the branch is behind the remote, and then pushes the current branch.

## Manual Checklist

Before pulling:

```cmd
git status --short --branch
git diff --stat
git pull --ff-only
```

Before pushing:

```cmd
git status --short --branch
git diff --stat
git add <intended-files>
git commit -m "type(scope): short message"
git push origin <branch>
```

Do not commit:

- `.env`
- real database passwords
- company/internal keys
- tokens or private credentials

Use `.env.example` and environment variables for sensitive configuration.

## Network Note From 2026-07-03

A later validation run reached the correct `git fetch origin --prune` step, but failed on network connectivity:

```text
Failed to connect to github.com port 443
```

`Test-NetConnection github.com -Port 443` showed `PingSucceeded: True` and `TcpTestSucceeded: False`. This means DNS and ping worked, but outbound HTTPS to GitHub was blocked or unavailable on the active network. Fix the network/proxy/VPN first, then rerun `scripts\git-pull.cmd` or `scripts\git-push.cmd`.
