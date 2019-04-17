#!/bin/bash
# This script can compile the code.
# The $? variable contains the return value of last command.
# Return value 0 is success; else failure.
# Author: ron jawanda
# usage: ./buildall.sh debug;./buildall.sh publishLocal
# this will create a .m2 directlry with a the aar residing in that direcory
# in your main application you then use implementation 'wildfire:wildlink:0.1000@aar' (android studio 3)
# in your buildscript respositories you must define mavenLocal() so it finds the aar file in your local m2.
# have fun


Command=$1

if [ "$Command" = "" ]; then
    echo "Error: Command is missing."
    exit 1
fi

if [ "$Command" = "ls" ]; then
    # list all the final binaries.
    Dirs="./wildlink/build/outputs/aar/"
    ls $Dirs 2> /dev/null  ## redirect all error messages to null
    exit 0
fi

if [ "$Command" = "clean" ]; then
    ./gradlew clean
    exit $?
fi

if [ "$Command" = "debug" ]; then
    # build debug binaries
    ./gradlew --info clean assembleDevDebug 
fi

if [ "$Command" = "release" ]; then
    # build release binaries
    ./gradlew clean assembleProdRelease
    exit $?
fi

if [ "$Command" = "lskeystore" ]; then
    # list the content of a keystore.
    KeystoreFilePath=$2
    keytool -list -v -keystore $KeystoreFilePath
    exit $?
fi

if [ "$Command" = "publish" ]; then
    # Upload libraries to Maven repository
    ./gradlew artifactoryPublish
    exit $?
fi

if [ "$Command" = "publishLocal" ]; then
    # Upload libraries to local Maven repository in ~/.m2/repository/
    ./gradlew publishToMavenLocal
    exit $?
fi

if [ "$Command" = "lint" ]; then
    # redirect stderr to stdout, then to pipe
    ./gradlew lint 2>&1 | grep -E "Wrote .+ report "
    exit 0
fi

if [ "$Command" = "gitlog" ]; then
    git log --oneline .
    exit $?
fi

echo "Error: Invalid command."
exit 1
