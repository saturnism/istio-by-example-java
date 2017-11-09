FROM openjdk:9-jre-slim
COPY target/work-server-1.0-SNAPSHOT.jar /app.jar

ENTRYPOINT ["java", "--add-modules", "jdk.incubator.httpclient", "-jar", "/app.jar"]
