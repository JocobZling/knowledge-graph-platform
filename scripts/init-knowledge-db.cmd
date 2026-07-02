@echo off
call "%~dp0backend-env.cmd"
createdb -h 127.0.0.1 -p 5432 -U postgres knowledge_graph
psql -h 127.0.0.1 -p 5432 -U postgres -d knowledge_graph -f "%~dp0..\docs\database.sql"
