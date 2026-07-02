@echo off
call "%~dp0backend-env.cmd"
if not exist "%CODEX_WORKSPACE%\logs" mkdir "%CODEX_WORKSPACE%\logs"
pg_ctl -D "%PGDATA%" -l "%CODEX_WORKSPACE%\logs\postgres.log" start
