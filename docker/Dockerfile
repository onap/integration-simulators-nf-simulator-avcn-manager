FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

ADD target/avcnmanager-${VERSION}.jar /app/avcnmanager.jar
CMD java -jar /app/avcnmanager.jar
