FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.13_8
MAINTAINER <lhstack@foxmail.com>
ENV JAVA_OPTS="-Xmx128m -Xms128m"
EXPOSE 9999
RUN mkdir -p /gateway/conf
ADD *.jar /gateway/app.jar
COPY conf /gateway/conf
COPY router /gateway/router
WORKDIR /gateway
CMD java $JAVA_OPTS -Dserver.port=9999 -jar app.jar
