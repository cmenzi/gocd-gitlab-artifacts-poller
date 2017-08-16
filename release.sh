#!/bin/bash
set -e -x

rm -rf dist/
mkdir dist

cd ../gocd-gitlab-artifacts-poller
mvn clean install -DskipTests -P gocd-gitlab-artifacts-poller
cp target/gocd-gitlab-artifacts-poller*.jar dist/