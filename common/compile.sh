#!/bin/sh
  
set -x

cd src
javac -d ../bin interfaces/*.java

test $? -eq 0 || exit 1

cd ../bin

jar cf ../common.jar interfaces/*.class

