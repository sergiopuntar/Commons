#!/usr/bin/env sh

if [ "$TRAVIS_JDK_VERSION" = "oraclejdk8" ];
then
   echo "Starting Sonar Analysis"
   mvn sonar:sonar -P sonar -Dsonar.organization=$SONAR_ORGANIZATION -Dsonar.login=$SONAR_TOKEN
else
   echo "Skipping Sonar Analysis"
fi