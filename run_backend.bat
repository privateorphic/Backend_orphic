@echo off
echo Starting Orphic Task Manager Backend...

if not exist "E:\CSharp" (
    mklink /J "E:\CSharp" "E:\C#" 2>nul
)

if exist "E:\CSharp\orphic Task manager\backend" (
    pushd "E:\CSharp\orphic Task manager\backend" 2>nul
) else (
    pushd "%~dp0"
)

echo Checking and freeing port 8080...
for /f "tokens=5" %%a in ('netstat -aon ^| findstr ":8080 " ^| findstr "LISTENING"') do (
    echo Terminating process PID %%a listening on port 8080...
    taskkill /F /PID %%a 2>nul
)
timeout /t 1 /nobreak >nul

set DB_HOST=localhost
set DB_PORT=3306
set DB_NAME=employee_management
set DB_USERNAME=root
set DB_PASSWORD=Devansh@2004
set JWT_SECRET=c2VjdXJlLWp3dC1zZWNyZXQta2V5LWZvci1lbXBsb3llZS1tYW5hZ2VtZW50LXN5c3RlbS0yMDI2LW11c3QtYmUtYXQtbGVhc3QtMjU2LWJpdHM=

set MAVEN_OPTS=--add-opens=jdk.compiler/com.sun.tools.javac.code=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.comp=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.file=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.main=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.model=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.parser=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.processing=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.tree=ALL-UNNAMED --add-opens=jdk.compiler/com.sun.tools.javac.util=ALL-UNNAMED

set INTELLIJ_MVN=C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.4\plugins\maven\lib\maven3\bin\mvn.cmd

if exist "%INTELLIJ_MVN%" (
    echo Using IntelliJ Bundled Maven...
    "%INTELLIJ_MVN%" spring-boot:run -Dmaven.test.skip=true
) else (
    echo Using System Maven...
    mvn spring-boot:run -Dmaven.test.skip=true
)
