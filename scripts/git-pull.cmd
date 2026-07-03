@echo off
setlocal

cd /d "%~dp0.."

echo [INFO] Checking repository status before pull...
git status --short --branch
if errorlevel 1 exit /b 1

for /f "delims=" %%S in ('git status --porcelain') do (
  echo [ERROR] Working tree is not clean. Commit, stash, or discard local changes before pulling.
  exit /b 1
)

echo [INFO] Fetching origin...
git fetch origin --prune
if errorlevel 1 exit /b 1

echo [INFO] Pulling with fast-forward only...
git pull --ff-only
if errorlevel 1 exit /b 1

echo [INFO] Final status:
git status --short --branch
