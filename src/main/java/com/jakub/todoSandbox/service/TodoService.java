package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import com.jakub.todoSandbox.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TodoService {
    private static final int MAX_NUM_STEPS = 10;
    private final TodoRepository todoRepository;

    public TodoService(TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    public void validateNameAndDesc(String name, String description) {
        if (name == null || name.isBlank() || name.length() > 100 || !name.matches("^[a-zA-Z0-9 ]+$") || description.length() >= 3000) {
            throw new ValidationException("Name is null, blank, has non-alphanumeric characters, " +
                    "or the name/description has exceeded the number of characters");
        }
    }

    public void canAddStepsToTodo(long id, List<Step> steps) {
        Todo todo = todoRepository.findTodoById(id)
                .orElseThrow((() -> new ValidationException("No Todo exists for the provided todoId: {" + id + "}.")));

        if (todo.steps().size() + steps.size() > 10) {
            throw new ValidationException("Error: Todo has reached its maximum number of steps (10).");
        }
    }


}


