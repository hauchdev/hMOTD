#!/usr/bin/env bash
# Build script for hMOTD on Unix-like systems
set -e

if [ -z "$JAVA_HOME" ]; then
    echo "JAVA_HOME is not set. Please set it to a JDK 21+ installation."
    exit 1
fi

if command -v mvn &> /dev/null; then
    MVN="mvn"
else
    MVN="$(dirname "$0")/.maven/apache-maven-3.10.0/bin/mvn"
fi

$MVN clean package
