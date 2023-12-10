FROM khipu/openjdk17-alpine

LABEL authors="choui"

LABEL maintainer="chouikhdaly215@gmail.com"

WORKDIR /bot

VOLUME /tmp

ARG JAR_FILE=target/deli.jar


COPY ${JAR_FILE} deli.jar


ENTRYPOINT ["java","-jar","/deli.jar"]