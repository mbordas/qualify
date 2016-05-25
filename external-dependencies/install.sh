#!/bin/bash

# diff-match-patch
mvn install:install-file -Dfile=diff_match_patch.jar -DgroupId=diff_match_patch -DartifactId=diff_match_patch -Dversion=1.0 -Dpackaging=jar

# sikuli-script
mvn install:install-file -Dfile=sikuli-script-1.0.1.jar -DgroupId=sikuli-script -DartifactId=sikuli-script -Dversion=1.0.1 -Dpackaging=jar

# tess4j
mvn install:install-file -Dfile=tess4j.jar -DgroupId=tess4j -DartifactId=tess4j -Dversion=1.0 -Dpackaging=jar

