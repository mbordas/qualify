#!/bin/bash

LIBSDIR=./external-dependencies

mvn install:install-file -Dfile="$LIBSDIR/diff_match_patch.jar" -DgroupId=diff_match_patch -Dversion=1.0 -DartifactId=diff_match_patch -Dpackaging=jar

mvn install:install-file -Dfile="$LIBSDIR/sikuli-script-1.0.1.jar" -DgroupId=sikuli-script -Dversion=1.0.1 -DartifactId=sikuli-script -Dpackaging=jar

