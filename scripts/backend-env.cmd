@echo off
set "CODEX_WORKSPACE=D:\codexWorkspace"
set "MAVEN_HOME=%CODEX_WORKSPACE%\tools\apache-maven-3.9.16"
set "POSTGRES_HOME=%CODEX_WORKSPACE%\tools\pgsql"
set "PGDATA=%CODEX_WORKSPACE%\postgres-data"
set "PGPASSWORD=postgres"
set "MAVEN_REPO=%CODEX_WORKSPACE%\m2-repository"
set "PATH=%MAVEN_HOME%\bin;%POSTGRES_HOME%\bin;%PATH%"
