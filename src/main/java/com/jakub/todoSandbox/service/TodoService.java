package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private static final int MAX_NUM_STEPS = 10;

    public void validateNameAndDesc(String name, String description) throws ValidationException {
        if (name != null && !name.isBlank() && name.length() <= 100 && description.length() < 3000) {
            boolean hasNonAlphanumeric = name.matches(".*[^a-zA-Z0-9].*");
            if (!hasNonAlphanumeric) {
                System.out.println("Name only has alphanumeric, description fits the maximum character limit - validation passed.");
                return;
            }
        }
        throw new ValidationException("Name is null, blank, has non-alphanumeric characters, " +
                "or the name/description has exceeded the number of characters");
    }

    public boolean canAddStepToTodo(Todo todo) {
        return todo.steps().size() < MAX_NUM_STEPS;
    }

    public Todo createNewTodoId(long id, Todo todo) {
        return new Todo(id, todo.name(), todo.description(), todo.priority(), todo.steps());
    }

    public List<Todo> sortByPriority(List<Todo> todos) {
        Comparator<Todo> priorityComparator = Comparator.comparing(Todo::priority);
        return todos.stream()
                .sorted(priorityComparator)
                .collect(Collectors.toList());
    }
}
