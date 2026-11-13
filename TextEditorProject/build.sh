#!/bin/sh
rm -rf out && mkdir out
javac -d out $(find src -name "*.java") && echo "Build OK. Run: java -cp out com.editor.Main"
