#!/bin/sh


cd bin
set -x
java -Djava.security.policy=../permissions -cp .:../common.jar peers.Publisher $*
