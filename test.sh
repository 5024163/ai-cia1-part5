#!/bin/sh
# Compiles the project together with the tests and runs all of them.
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out -sourcepath src:test test/astar/AlgorithmTests.java test/astar/ControllerTests.java
java -cp out astar.AlgorithmTests
java -cp out astar.ControllerTests
