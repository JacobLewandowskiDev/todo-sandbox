# Todo Sandbox

You need to create a restful API using following technologies:
* Java 17
* Spring Boot
* Maven
* JOOQ (for talking with the database): https://www.jooq.org/
* Postgresql as database
* Testcontainers for db/integration tests: https://www.testcontainers.org/

The API should allow to work with the following TODO model:
```
{
    "id": Long,
    "name": String,
    "description": String,
    "priority": Enum with the options: LOW, MEDIUM, HIGH,
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

## Endpoints
1. POST to create Todo, returning its id
2. PUT to update TODO (without steps)
3. POST to add step to TODO
4. PUT to update TODO's step
5. DELETE to delete TODO's step
6. DELETE to delete single TODO
7. GET to get single TODO with all its steps
8. GET to list all TODOs (without steps and description), ordered by the highest priority

In case of validation (or another) error, API should return proper http code with some useful information about the error.

## Implementantion
Spring Boot app with tests, talking with Postgres database.

It should be possible to run it locally in the docker container, together with dockerized Postgres.

App should have both unit tests of bussiness rules and logic as well as integration tests, using Testcontainers.

Integration tests should test controllers, making real http calls and using real databse (use Testcontainers for that).

We have Java 17, so use its features, especially records and var for local variables.

