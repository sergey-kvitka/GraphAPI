#!/bin/bash

mvn clean package

wget https://download.bell-sw.com/java/17.0.3+7/bellsoft-jre17.0.3+7-windows-amd64.zip
unzip bellsoft-jre17.0.3+7-windows-amd64.zip
mv jre-17.0.3 jre-win
wget https://download.bell-sw.com/java/17.0.3+7/bellsoft-jre17.0.3+7-linux-amd64.tar.gz
tar xvzf bellsoft-jre17.0.3+7-linux-amd64.tar.gz
mv jre-17.0.3 jre-linux

#WINDOWS
mkdir target/win
cp target/*.jar target/win/app.jar
cp -r jre-win target/win/
cp target-scripts/run-win.bat target/win/

#LINUX
mkdir target/lin
cp target/*.jar target/lin/app.jar
cp -r jre-linux target/lin/
cp target-scripts/run-linux.sh target/lin/
chmod +rwx target/lin/jre-linux/bin/java
chmod +rwx target/lin/run-linux.sh