#!/bin/bash

set -e

ARCH="$(arch)"

if [[ "${ARCH}" != "x86_64" ]]; then
  MODE="jvm"
  echo "Starting gateway in 'jvm' mode because 'native' is not supported for '${ARCH}'"
else
  MODE=${1:-"jvm"}
  echo "Starting gateway in '${MODE}' mode"
fi

if [[ "${MODE}" == "jvm" ]]; then
  java -Dquarkus.http.host=0.0.0.0 -Djava.util.logging.manager=org.jboss.logmanager.LogManager -jar /gateway/quarkus-run.jar
elif [[ "${MODE}" == "native" ]]; then
  /gateway/quarkus-run -Dquarkus.http.host=0.0.0.0
else
  echo "Invalid type, should be either 'jvm' or 'native'"
  exit 1
fi
