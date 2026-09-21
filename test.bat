@echo off
cd /d "%~dp0"
if not exist out mkdir out
javac -d out -sourcepath "src;test" test\astar\AlgorithmTests.java test\astar\ControllerTests.java
if errorlevel 1 exit /b 1
java -cp out astar.AlgorithmTests
java -cp out astar.ControllerTests
