FROM docker.io/openjdk:11-jre-slim

ARG VERSION=${version}

ADD target/avcn-manager-${VERSION}.jar /app/avcn-manager.jar
CMD java -jar /app/avcn-manager.jar
