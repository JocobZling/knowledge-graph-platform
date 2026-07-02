@echo off
call "%~dp0backend-env.cmd"
psql -h 127.0.0.1 -p 5432 -U postgres -d knowledge_graph -c "select current_database(), count(*) as daily_brief_count from daily_brief;"
