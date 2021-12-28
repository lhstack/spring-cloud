FROM adoptopenjdk/openjdk11:x86_64-alpine-jre-11.0.13_8
MAINTAINER <lhstack@foxmail.com>
ENV JAVA_OPTS="-Xmx128m -Xms128m"
EXPOSE 8761
RUN mkdir -p /eureka/conf
ADD eureka-server-0.0.1.jar /eureka/app.jar
COPY conf /eureka/conf
WORKDIR /eureka
CMD java $JAVA_OPTS -Dserver.port=8761 -jar app.jar
