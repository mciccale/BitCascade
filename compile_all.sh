#!/bin/sh

cd `dirname $0`
for dir in common tracker_node peer_node
do
    test -d $dir || continue
    echo compiling $dir
    cd $dir
    ./compile.sh
    test $? -eq 0 || exit 1
    cd ..
    echo
    echo
done

