#!/usr/bin/env sh

if [ "$TRAVIS_JDK_VERSION" == "oraclejdk8" ]; then
   mvn sonar:sonar -P sonar -Dsonar.organization=$SONAR_ORGANIZATION -Dsonar.login=$SONAR_TOKEN
fi