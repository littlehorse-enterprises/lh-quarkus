#!/bin/bash

set -e

SCRIPT_DIR=$(cd "$(dirname "$0")" && pwd)

export ORG_GRADLE_PROJECT_signingKey="$(<$SCRIPT_DIR/eng.key)"
export ORG_GRADLE_PROJECT_signingPassword=""
export ORG_GRADLE_PROJECT_sonatypeUsername=""
export ORG_GRADLE_PROJECT_sonatypePassword=""

./gradlew publishToSonatype --console plain
