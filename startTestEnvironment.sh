#!/bin/bash

#
# Starts a Chatty server and two independent Chatty clients
#

if ! [ -f "pom.xml" ]
then
  echo "Please go to the root of this project"
  exit 1
fi

java -jar target/chatty-server-1.0.0-SNAPSHOT.jar 5050 &
java -jar target/chatty-clientgui-1.0.0-SNAPSHOT.jar &
java -jar target/chatty-clientgui-1.0.0-SNAPSHOT.jar &