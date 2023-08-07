package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository {
    // TodoMethods
    Optional<Todo> findTodoById(long todoId);
    List<Todo> findAllTodos();
    Todo saveTodo(Todo todo) throws ValidationException;
    Optional<Todo> deleteTodo(long todoId);
    void updateTodo(long todoId, Todo todo) throws ValidationException;

    // Step Methods
    void saveSteps(long todoId, List<Step> createdSteps) throws ValidationException;
    void deleteSteps(long todoId, List<Long> stepId) throws ValidationException;
    void updateStep(long todoId, Step updatedStep) throws ValidationException;
}
