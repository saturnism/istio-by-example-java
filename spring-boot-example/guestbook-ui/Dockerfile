FROM openjdk:8

COPY target/lib/* /app/lib/
COPY target/helloworld-ui-0.0.1-SNAPSHOT.jar /app/app.jar

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
