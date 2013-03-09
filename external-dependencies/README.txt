Following dependencies are not available on a Maven repository:
qualify
sikuli-script
tess4j


Make sure they are installed into your local maven repository by running:
mvn install:install-file -Dfile=<path-to-jar> -DgroupId=<groupId> -DartifactId=<artifactId> -Dversion=<version> -Dpackaging=jar
