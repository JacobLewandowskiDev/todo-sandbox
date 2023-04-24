package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TodoRepository {
    // TodoMethods
    Optional<Todo> findTodoById(Long todoId);
    List<Todo> findAllTodos();
    Todo saveTodo(Todo todo);
    Todo deleteTodo(Long todoId);
    void updateTodo(Long todoId, Todo todo);

    // Step Methods
    void saveSteps(Long todoId, List<Step> createdSteps);
    void deleteSteps(Long todoId, List<Long> stepIds);
    void updateStep(Long todoId, int oldStepId, Step updatedStep);
}
