package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class TodoService {

    // Temporary Map for todos - for testing purposes TODO implement Postgres database for todos and steps instead of map
    private Map<Long, Todo> todoList;
    private static final int MAX_NUM_STEPS = 10;


    public TodoService(Map<Long, Todo> todoList) {
        this.todoList = todoList;
    }

    public Todo createTodo(Todo todo) {
        if (validateNameAndDesc(todo.getName(), todo.getDescription())) {
            todo.setId(todoList.size() + 1L);
            todoList.put(todo.getId(), todo);
            System.out.println(
                    "New todo was created: \n name: " + todo.getName()
                            + "\n description: " + todo.getDescription()
                            + "\n priority: " + todo.getPriority()
                            + "\n steps: \n" + todo.getsteps().get(0).getName());
            return todo;
        } else {
            return null;
        }
    }

    // Update only the main to-do, not its steps
    public void updateTodo(Long id, Todo todo) {
        Todo exists = todoList.get(id);
        if (exists != null) {
            exists.setName(todo.getName());
            exists.setDescription(todo.getDescription());
            exists.setPriority(todo.getPriority());
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
            if (todo.getId().equals(todoId)) {
                return todoList.get(todoId);
            }
        }
        return null;
    }

    public void createStep(Long todoId, List<Step> createdSteps) {
        Todo exists = todoList.get(todoId);
        if (exists != null) {
            System.out.println("Size of step array before adding new: " + exists.getsteps().size());
            for (Step step : createdSteps) {
                if (exists.getsteps().size() < MAX_NUM_STEPS) {
                    step.setId(exists.getsteps().size() + 1L);
                    exists.getsteps().add(step);
                } else {
                    break;
                }
            }
            System.out.println("Size of step array after adding new: " + exists.getsteps().size());
        } else {
            System.out.println("Exceeded maximum number of steps of 10 for todo, or todo does not exist.");
        }
    }

    public void deleteStep(Long todoId, List<Long> stepIds) {
        Todo exists = todoList.get(todoId);
        if (exists != null && stepIds.size() > 0 && !stepIds.contains(0)) {
            System.out.println("Size of step array before removing step: " + exists.getsteps().size());
            Iterator<Step> iterator = exists.getsteps().iterator();
            while (iterator.hasNext()) {
                Step step = iterator.next();
                if (stepIds.contains(step.getId())) {
                    iterator.remove();
                    System.out.println("Step with id:" + step.getId() + " was removed.");
                }
            }
            System.out.println("Size of step array after removing step: " + exists.getsteps().size());
        } else {
            System.out.println("No such todo with this id exists.");
        }
    }

    public void updateStep(Long todoId, int oldStepId, Step updatedStep) {
        Todo exists = todoList.get(todoId);
        if (exists != null && oldStepId >= 0) {
            for (Step step : exists.getsteps()) {
                if (step.getId() == oldStepId) {
                    step.setName(updatedStep.getName());
                    step.setDescription(updatedStep.getDescription());
                    System.out.println("Step with id:" + oldStepId + " has been updated");
                    break;
                }
            }
        } else {
            System.out.println("No such todo or step exists.");
        }
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
        Comparator<Todo> priorityComparator = Comparator.comparing(Todo::getPriority);
        return todos.stream()
                .sorted(priorityComparator)
                .collect(Collectors.toList());
    }
}
