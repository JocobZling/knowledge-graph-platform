@echo off
call "%~dp0backend-env.cmd"
cd /d "%~dp0..\backend"
mvn -Dmaven.repo.local="%MAVEN_REPO%" spring-boot:run
