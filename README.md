# Virtual Task & Productivity Tracker Project - Backend

This project is the backend portion of a full-stack Virtual Task & Productivity Tracker application built with Java 17. It provides a RESTful API for the frontend, allowing CRUD operations to modify the PostgreSQL database containing Virtual Tasks & their associated Steps.

## Technologies used for this project:
* Java 17,
* Spring Boot,
* Maven,
* JOOQ (for talking with the database): https://www.jooq.org/
* Postgresql as the database,
* Testcontainers for db/integration tests: https://www.testcontainers.org/

## Todo Model

The API works with the following Todo model:

    ```json
        {
            "id": Long,
            "name": String,
            "description": String,
            "priority": Enum with options: LOW, MEDIUM, HIGH,
            "steps": [
                {
                    "id": Long,
                    "name": String,
                    "description": String
                }
            ]
        }
    ```

## Validation rules
1. Name: only alphanumeric characters, not blank, not null, max 100 characters
2. Description is optional, but if given it should have less than 3000 characters
3. Priority is required and should have only one of these three values: LOW, MEDIUM, HIGH
4. Steps are optional (TODO without steps is allowed), but when given, name and description should be validated as above, and there can be max 10 steps for a single TODO

## API Endpoints
This application interacts with the following API endpoints:
* (GET) GetAllTodos: /todos                                   :List all Todos (without steps and description), ordered by highest priority, 
* (GET) GetTodoById: /todos/${id}                             :Get a single Todo with all its steps,
* (POST) CreateTodo: /todos                                   :Create a Todo, returning its ID, 
* (PUT) UpdateTodo: /todos/${id}                              :Update a Todo (without steps),
* (DELETE) DeleteTodo: /todos/${id}                           :Delete a single Todo, 
* (POST) SaveStep: /todos/${id}/steps                         :Add steps to a Todo's step list, 
* (PUT) UpdateStep: /todos/${id}/steps                        :Update a Todo's step, 
* (DELETE) DeleteStep: /todos/${id}/steps?stepId=${stepId}    :Delete a Todo's step,

In case of a validation (or another) error, the API should return the proper HTTP code with useful information about the error.

## Implementantion

* Spring Boot application with tests.
* Communication with PostgreSQL database using JOOQ.
* Unit tests for business rules and logic.
* Integration tests with Testcontainers for controllers and real HTTP calls with a real database.

## Running the Application Locally
To run the application locally, follow these steps:
1. Make sure you have Java 17 installed on your system.

2. Setup a PostgreSQL Database:
     * Create a Postgres database and run the schema below:
       ```
        DROP TABLE IF EXISTS step;
        DROP TABLE IF EXISTS todo;
        DROP TYPE IF EXISTS priority_enum;
        
        CREATE TYPE priority_enum AS ENUM ('LOW', 'MEDIUM', 'HIGH');
        
        CREATE TABLE IF NOT EXISTS todo (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            description TEXT,
            priority priority_enum NOT NULL
        );
        
        CREATE INDEX idx_todo_name ON todo (name);
        
        CREATE TABLE IF NOT EXISTS step (
            id BIGSERIAL PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            description TEXT,
            todo_id BIGINT NOT NULL REFERENCES todo (id) ON DELETE CASCADE
        );
        
        CREATE INDEX idx_step_todo_id ON step (todo_id);
       ```
    * Set up your local environment variables:
          ```
            ${env.JDBC_DRIVER}
          ```
          ```
            ${env.JDBC_URL}
          ```
          ```
          ${env.JDBC_USERNAME}
          ```
          ```
          ${env.JDBC_PASSWORD}
          ```
3. Clone this repository.
4. Navigate to the project directory.
5. Build the project using Maven with the dev profile:
   ```
    mvn clean install -Pdev
   ```
6. Run the Application
    ```
    java -jar target/<jar-file-name>.jar
    ```
7. The Application will now run on your localhost:8080
*To use the Frontend portion of the Application locally you must configure the CorsConfig class to use the same port as the frontend.

## Frontend portion of the Application
To use the frontend portion of the application, please follow the link below to learn how to set up the app:
https://github.com/JacobLewandowskiDev/todo-sandbox-front.git


