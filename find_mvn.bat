@echo off
dir "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.4\plugins\maven\lib\maven3\bin\mvn.cmd" 2>nul
if errorlevel 1 (
    echo NOT FOUND in maven3\bin
    dir /s /b "C:\Program Files\JetBrains\IntelliJ IDEA 2026.1.4\*mvn.cmd" 2>nul
) else (
    echo FOUND INTELLIJ MVN!
)
