#!/bin/bash

sudo apt install -y unzip zip

mvn clean package
wget https://download.bell-sw.com/java/17.0.3+7/bellsoft-jre17.0.3+7-windows-amd64.zip
unzip bellsoft-jre17.0.3+7-windows-amd64.zip
mv jre-17.0.3 jre

#WINDOWS
mkdir target/win
copy target/*.jar target/win/app.jar
copy -r jre target/win/app/
copy target-scripts/run-win.bat target/win/
zip win-dist.zip target/win/
