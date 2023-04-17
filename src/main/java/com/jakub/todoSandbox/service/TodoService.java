package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.repository.TodoRepository;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TodoService {

    // Temporary Map for todos - for testing purposes TODO implement Postgres database for todos and steps instead of map
    private Map<Long, Todo> todoList;
    private final TodoRepository todoRepository;
    private static final int MAX_NUM_STEPS = 10;


    public TodoService(Map<Long, Todo> todoList, TodoRepository todoRepository) {
        this.todoList = todoList;
        this.todoRepository = todoRepository;
    }

    public Todo createTodo(Todo todo) {
        if (validateNameAndDesc(todo.name(), todo.description())) {
            todo.setId(todoList.size() + 1L);
            todoList.put(todo.getId(), todo);
            System.out.println(
                    "New todo was created: \n name: " + todo.name()
                            + "\n description: " + todo.description()
                            + "\n priority: " + todo.priority()
                            + "\n steps: \n" + todo.steps().get(0).name());
            return todo;
        } else {
            return null;
        }
    }

    // Update only the main to-do, not its steps
    public void updateTodo(Long id, Todo todo) {
        var exists = todoList.get(id);
        if (exists != null) {
            exists.setName(todo.name());
            exists.setDescription(todo.description());
            exists.setPriority(todo.priority());
            todoList.put(id, exists);
            System.out.println("Todo using id: {" + id + "} has been updated.");
        } else {
            System.out.println("No todo exists under this id.");
        }
    }

    public void deleteTodo(Long id) {
        if (todoList.get(id) != null) {
            todoList.remove(id);
            System.out.println("Todo with the id: " + id + " was removed.");
        } else {
            System.out.println("There isn't any todo under id: " + id);
        }
    }

    public List<Todo> getTodoList() {
        List<Todo> todoMapToList = new ArrayList<>(todoList.values());
        return sortByPriority(todoMapToList);
    }

    public Todo getTodoById(Long todoId) {
        for (Todo todo : todoList.values()) {
            if (todo.id().equals(todoId)) {
                return todoList.get(todoId);
            }
        }
        return null;
    }

    public void createStep(Long todoId, List<Step> createdSteps) {
        var exists = todoList.get(todoId);
        if (exists != null) {
            System.out.println("Size of step array before adding new: " + exists.steps().size());
            for (Step step : createdSteps) {
                if (canAddStep(exists)) {
                    step.setId(exists.steps().size() + 1L);
                    exists.steps().add(step);
                } else {
                    System.out.println("You have reached the maximum number of steps of 10 for todo with id: " + exists.id());
                    break;
                }
            }
            System.out.println("Size of step array after adding new: " + exists.steps().size());
        } else {
            System.out.println("Such a Todo does not exist.");
        }
    }

    public void deleteStep(Long todoId, List<Long> stepIds) {
        var exists = todoList.get(todoId);
        if (exists != null && stepIds.size() > 0 && !stepIds.contains(0)) {
            System.out.println("Size of step array before removing step: " + exists.steps().size());
            Iterator<Step> iterator = exists.steps().iterator();
            while (iterator.hasNext()) {
                Step step = iterator.next();
                if (stepIds.contains(step.id())) {
                    iterator.remove();
                    System.out.println("Step with id:" + step.id() + " was removed.");
                }
            }
            System.out.println("Size of step array after removing step: " + exists.steps().size());
        } else {
            System.out.println("No such todo with this id exists.");
        }
    }

    public void updateStep(Long todoId, int oldStepId, Step updatedStep) {
        var exists = todoList.get(todoId);
        if (exists != null && oldStepId >= 0) {
            for (Step step : exists.steps()) {
                if (step.id() == oldStepId) {
                    step.setName(updatedStep.name());
                    step.setDescription(updatedStep.description());
                    System.out.println("Step with id:" + oldStepId + " has been updated");
                    break;
                }
            }
        } else {
            System.out.println("No such todo or step exists.");
        }
    }

    private boolean canAddStep(Todo todo) {
        return todo.steps().size() < MAX_NUM_STEPS;
    }

    private boolean validateNameAndDesc(String name, String description) {
        if (!name.isEmpty() && !name.trim().isEmpty() && name.length() <= 100 && description.length() < 3000) {
            boolean hasAlpha = name.matches("^.*[^a-zA-Z0-9 ].*$");
            if (!hasAlpha) {
                System.out.println("Name only has alphanumeric");
                return true;
            }
        } else
            System.out.println("Has non alphanumeric symbol or name is null");
        return false;
    }

    private List<Todo> sortByPriority(List<Todo> todos) {
        Comparator<Todo> priorityComparator = Comparator.comparing(Todo::priority);
        return todos.stream()
                .sorted(priorityComparator)
                .collect(Collectors.toList());
    }
}
