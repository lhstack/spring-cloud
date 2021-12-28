FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.13_8
MAINTAINER <lhstack@foxmail.com>
ENV JAVA_OPTS="-Xmx128m -Xms128m"
EXPOSE 7000
RUN mkdir -p /config-server/conf
ADD *.jar /config-server/app.jar
COPY conf /config-server/conf
WORKDIR /config-server
CMD java $JAVA_OPTS -Dserver.port=7000 -jar app.jar
