#!/bin/bash
# Compile and run the Library Management System (needs JDK 17 or newer)
mkdir -p out
javac -d out $(find src -name "*.java") || exit 1
java -cp "out:lib/sqlite-jdbc.jar" com.library.Main
