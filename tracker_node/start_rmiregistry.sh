#!/bin/sh

set -x
CLASSPATH=.:common.jar exec rmiregistry $*
