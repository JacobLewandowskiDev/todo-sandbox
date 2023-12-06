package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository {
    // TodoMethods
    Optional<Todo> findTodoById(long todoId);
    List<Todo> findAllTodos();
    long saveTodo(Todo todo);
    Optional<Todo> deleteTodo(long todoId);
    void updateTodo(long todoId, Todo todo);

    // Step Methods
    void saveSteps(long todoId, List<Step> createdSteps);
    void deleteSteps(long todoId, List<Long> stepId);
    void updateStep(long todoId, Step updatedStep);
}
