FROM openjdk:17.0.1-jdk-slim

MAINTAINER "neo.zhang"

ENV PARAMS = ""

ENV TZ = PRC

# RUN ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && echo $TZ > /etc/timezone

ADD target/neo-chat-app.jar /neo-chat-app.jar

ENTRYPOINT ["sh","-c","java -jar $JAVA_OPTS /neo-chat-app.jar $PARAMS"]