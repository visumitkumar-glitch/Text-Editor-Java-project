@echo off
if exist out rmdir /s /q out
mkdir out
dir /s /b src\*.java > sources.txt
javac -d out @sources.txt
del sources.txt
echo Build OK. Run: java -cp out com.editor.Main
