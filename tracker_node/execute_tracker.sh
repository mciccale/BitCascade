#!/bin/sh

set -x

cd bin
java -Djava.security.policy=../permissions -cp .:../common.jar tracker.TrackerSrv $*
