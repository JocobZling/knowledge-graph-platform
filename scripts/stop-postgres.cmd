@echo off
call "%~dp0backend-env.cmd"
pg_ctl -D "%PGDATA%" stop
