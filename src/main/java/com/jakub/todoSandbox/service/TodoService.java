package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TodoService {
    private static final int MAX_NUM_STEPS = 10;

    public boolean validateNameAndDesc(String name, String description) throws ValidationException {
        if (name != null && !name.isBlank() && name.length() <= 100 && description.length() < 3000) {
            boolean hasNonAlphanumeric = name.matches("^.*[^a-zA-Z0-9 ].*$");
            if (!hasNonAlphanumeric) {
                System.out.println("Name only has alphanumeric, description fits the maximum character limit - validation passed.");
                return true;
            }
        }
        throw new ValidationException("Name is null, blank, has non-alphanumeric characters, " +
                "or the name/description has exceeded the number of characters");
    }

    public boolean canAddStepToTodo(Todo todo) {
        return todo.steps().size() < MAX_NUM_STEPS;
    }

    public Step createNewStepForTodo(Todo todo, Step step) {
        Long newStepId = (long) (todo.steps().size() + 1);
        return new Step(newStepId, step.name(), step.description());
    }

    public Todo createNewTodoId(Long id, Todo todo) {
        return new Todo(id, todo.name(), todo.description(), todo.priority(), todo.steps());
    }

    public List<Todo> sortByPriority(List<Todo> todos) {
        Comparator<Todo> priorityComparator = Comparator.comparing(Todo::priority);
        return todos.stream()
                .sorted(priorityComparator)
                .collect(Collectors.toList());
    }
}
