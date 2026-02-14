# First build stage
FROM eclipse-temurin:17-jre-alpine AS builder

# Update apk and install any required dependencies
RUN apk update && apk add --no-cache curl

# Copy the built JAR file from your local machine to the container
COPY target/todo-app.jar todo-app.jar

# Final build stage
FROM eclipse-temurin:17-jre-alpine

# Add a non-root user for running the application
RUN addgroup -S appgroup && \
    adduser -S appuser -G appgroup
USER appuser

# Copy only the necessary files from the builder stage
COPY --from=builder /todo-app.jar /todo-app.jar

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "todo-app.jar"]