FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.13_8
MAINTAINER <lhstack@foxmail.com>
ENV JAVA_OPTS="-Xmx128m -Xms128m"
EXPOSE 8800
RUN mkdir -p /admin/conf
ADD *.jar /admin/app.jar
COPY conf /admin/conf
WORKDIR /admin
CMD java $JAVA_OPTS -Dserver.port=8800 -jar app.jar
