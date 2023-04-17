package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoRepository {
    // TodoMethods
    Todo findTodoById(Long todoId);
    List<Todo> findAllTodos();
    Todo saveTodo(Todo todo);
    void deleteTodo(Long todoId);
    void updateTodo(Long todoId, Todo todo);

    // Step Methods
    void saveSteps(Long todoId, List<Step> createdSteps);
    void deleteSteps(Long todoId, List<Long> stepIds);
    void updateStep(Long todoId, int oldStepId, Step updatedStep);
}
