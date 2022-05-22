#!/bin/bash

mvn clean package
wget https://download.bell-sw.com/java/17.0.3+7/bellsoft-jre17.0.3+7-windows-amd64.zip
unzip bellsoft-jre17.0.3+7-windows-amd64.zip
mv jre-17.0.3 jre

#WINDOWS
mkdir target/win
cp target/*.jar target/win/app.jar
cp -r jre target/win/
cp target-scripts/run-win.bat target/win/
