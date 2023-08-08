package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private static final int MAX_NUM_STEPS = 10;

    public void validateNameAndDesc(String name, String description) {
        if (name == null || name.isBlank() || name.length() > 100 || description.length() >= 3000) {
            throw new ValidationException("Name is null, blank, has non-alphanumeric characters, " +
                    "or the name/description has exceeded the number of characters");
        }

        boolean hasNonAlphanumeric = name.matches(".*[^a-zA-Z0-9].*");
        if (!hasNonAlphanumeric) {
            System.out.println("Name only has alphanumeric, description fits the maximum character limit - validation passed.");
        } else {
            throw new ValidationException("Name contains non-alphanumeric characters.");
        }
    }

    public void canAddStepToTodo(Todo todo, List<Step> steps) {
        if (todo.steps().size() < MAX_NUM_STEPS) {
            for (Step step : steps) {
                validateNameAndDesc(step.name(), step.description());
            }
        }
        else {
            throw new ValidationException("Error: Todo has reached its maximum number of steps (10).");
        }
    }
}
