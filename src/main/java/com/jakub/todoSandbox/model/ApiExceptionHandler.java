package com.jakub.todoSandbox.model;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTodoException(ValidationException ex) {
        return ResponseEntity.badRequest().body(ErrorResponse.fromException(ex));
    }
}
