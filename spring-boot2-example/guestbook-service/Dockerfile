FROM openjdk:8u131-jre-alpine

COPY target/lib/* /app/lib/
COPY target/guestbook-service-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
