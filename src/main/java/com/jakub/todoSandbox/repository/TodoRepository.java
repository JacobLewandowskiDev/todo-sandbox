package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.jooq.sample.model.tables.records.TodoRecord;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository {
    // TodoMethods
    Optional<Todo> findTodoById(Long todoId);
    List<Todo> findAllTodos();
    Todo saveTodo(Todo todo) throws ValidationException;
    Optional<Todo> deleteTodo(Long todoId);
    void updateTodo(Long todoId, Todo todo) throws ValidationException;

    // Step Methods
    void saveSteps(Long todoId, List<Step> createdSteps) throws ValidationException;
    void deleteSteps(Long todoId, List<Long> stepIds) throws ValidationException;
    void updateStep(Long todoId, Step updatedStep) throws ValidationException;
}
