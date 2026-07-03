@echo off
setlocal

cd /d "%~dp0.."

echo [INFO] Checking repository status before push...
git status --short --branch
if errorlevel 1 exit /b 1

for /f "delims=" %%S in ('git status --porcelain') do (
  echo [ERROR] Working tree is not clean. Commit, stash, or discard local changes before pushing.
  exit /b 1
)

for /f "delims=" %%F in ('git ls-files') do (
  if /i "%%~nxF"==".env" (
    echo [ERROR] Tracked .env file found: %%F
    echo [ERROR] Do not push real environment files. Use .env.example instead.
    exit /b 1
  )
)

for /f "delims=" %%B in ('git branch --show-current') do set "BRANCH=%%B"
if "%BRANCH%"=="" (
  echo [ERROR] Could not determine current branch.
  exit /b 1
)

echo [INFO] Fetching origin...
git fetch origin --prune
if errorlevel 1 exit /b 1

for /f "tokens=1,2" %%A in ('git rev-list --left-right --count origin/%BRANCH%...HEAD') do (
  set "BEHIND=%%A"
  set "AHEAD=%%B"
)

if "%BEHIND%"=="" (
  echo [ERROR] Could not compare local branch with origin/%BRANCH%.
  exit /b 1
)

if not "%BEHIND%"=="0" (
  echo [ERROR] Local branch is behind origin/%BRANCH% by %BEHIND% commit(s).
  echo [ERROR] Run scripts\git-pull.cmd first, resolve any conflicts, then push again.
  exit /b 1
)

if "%AHEAD%"=="0" (
  echo [INFO] Nothing to push. Local branch is already aligned with origin/%BRANCH%.
  exit /b 0
)

echo [INFO] Pushing %AHEAD% commit(s) to origin/%BRANCH%...
git push origin %BRANCH%
if errorlevel 1 exit /b 1

echo [INFO] Final status:
git status --short --branch
