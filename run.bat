@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out -sourcepath src src\astar\Main.java
if errorlevel 1 exit /b 1
java -cp out astar.Main
