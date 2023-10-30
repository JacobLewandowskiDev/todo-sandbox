FROM openjdk:17-jdk-slim

COPY target/todo-app.jar todo-app.jar

Expose 8080

ENTRYPOINT ["java", "-jar", "todo-app.jar"]