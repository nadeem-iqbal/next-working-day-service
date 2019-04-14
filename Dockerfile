FROM openjdk:8-jdk-alpine

VOLUME /tmp

ARG JAR_FILE
COPY ${JAR_FILE} next-working-day-app.jar

ENTRYPOINT ["java","-jar","/next-working-day-app.jar"]

EXPOSE 8080