#!/bin/sh
# Compiles the project and starts the simulator (needs JDK 17 or newer).
set -e
cd "$(dirname "$0")"
mkdir -p out
javac -d out -sourcepath src src/astar/Main.java
java -cp out astar.Main
