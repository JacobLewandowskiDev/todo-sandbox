package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import com.jakub.todoSandbox.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TodoService {
    private static final int MAX_NUM_STEPS = 10;
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public Optional<Todo> findTodoById(long todoId) {
        System.out.println("findTodoById() method call with todoId: {" + todoId + "}.");
        return todoRepository.findTodoById(todoId);
    }

    public List<Todo> findAllTodos() {
        System.out.println("findAllTodos() method call");
        return todoRepository.findAllTodos();
    }

    public Todo saveTodo(Todo todo) {
        validateNameAndDesc(todo.name(), todo.description());
        if (!todo.steps().isEmpty()) {
            for (Step step : todo.steps()) {
                validateNameAndDesc(step.name(), step.description());
            }
        }
        return todoRepository.saveTodo(todo);
    }

    public void updateTodo(long todoId, Todo todo) {
        System.out.println("updateTodo() method call");
        if (todoRepository.findTodoById(todoId).isPresent()) {
            todoRepository.updateTodo(todoId, todo);
        } else {
            throw new ValidationException("No todo exists under todoId: {" + todoId + "}");
        }
    }

    public Optional<Todo> deleteTodo(long todoId) {
        System.out.println("deleteTodo() method call with todoId: " + todoId + ".");
        return todoRepository.deleteTodo(todoId);
    }

    public void saveSteps(long todoId, List<Step> steps) {
        System.out.println("saveSteps() method called with todoId: {" + todoId + "}.");
        for (Step step : steps) {
            validateNameAndDesc(step.name(), step.description());
        }
        canAddStepsToTodo(todoId, steps);
        todoRepository.saveSteps(todoId, steps);
    }

    public void updateStep(long todoId, Step updatedStep) {
        System.out.println("updateStep() method called with todoId: " + todoId);
        validateNameAndDesc(updatedStep.name(), updatedStep.description());
        todoRepository.updateStep(todoId, updatedStep);
    }

    public void deleteSteps(long todoId, List<Long> stepIds) {
        System.out.println("deleteSteps() method called with todoId: {" + todoId + "}.");
        todoRepository.deleteSteps(todoId, stepIds);
    }

    public void validateNameAndDesc(String name, String description) {
        if (name == null || name.isBlank() || name.length() > 100 || !name.matches("^[a-zA-Z0-9 ]+$")) {
            throw new ValidationException("Name is null, blank, has non-alphanumeric characters, " +
                    "or the name has exceeded the number of characters");
        }
        if (description.length() >= 3000) {
            throw new ValidationException("The description has exceeded the number of characters");
        }
    }

    public void canAddStepsToTodo(long id, List<Step> steps) {
        Todo todo = todoRepository.findTodoById(id)
                .orElseThrow((() -> new ValidationException("No Todo exists for the provided todoId: {" + id + "}.")));

        if (todo.steps().size() + steps.size() > MAX_NUM_STEPS) {
            throw new ValidationException("Error: Todo has reached its maximum number of steps (10).");
        }
    }

}


