package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class InMemoryRepository implements TodoRepository{

    private final Map<Long, Todo> todoList = new HashMap<Long, Todo>();
    private static final int MAX_NUM_STEPS = 10;
    private Long maxTodoId = 0L;

    @Override
    public Todo findTodoById(Long todoId) {
        for (Todo todo : todoList.values()) {
            if (todo.id().equals(todoId)) {
                return todoList.get(todoId);
            }
        }
        return null;
    }

    @Override
    public List<Todo> findAllTodos() {
        List<Todo> todoMapToList = new ArrayList<>(todoList.values());
        return sortByPriority(todoMapToList);
    }

    @Override
    public Todo saveTodo(Todo todo) {
        if (validateNameAndDesc(todo.name(), todo.description())) {
            var toBeSavedTodo = new Todo((maxTodoId + 1L), todo.name(), todo.description(), todo.priority(), todo.steps());
            todoList.put(toBeSavedTodo.id(), toBeSavedTodo);
            maxTodoId = toBeSavedTodo.id();
            System.out.println(
                    "New todo was created:" +
                              "\n id: " + toBeSavedTodo.id()
                            + " \n name: " + toBeSavedTodo.name()
                            + "\n description: " + toBeSavedTodo.description()
                            + "\n priority: " + toBeSavedTodo.priority()
                            + "\n steps: \n" + toBeSavedTodo.steps().get(0).name());
            return toBeSavedTodo;
        } else {
            return null;
        }
    }

    @Override
    public void updateTodo(Long todoId, Todo todo) {
        var exists = todoList.get(todoId);
        if (exists != null) {
            todoList.put(todoId, createNewTodoId(todoId, todo));
            System.out.println("Todo using id: {" + todoId + "} has been updated.");
        } else {
            System.out.println("No todo exists under this id.");
        }
    }

    @Override
    public void deleteTodo(Long todoId) {
        if (todoList.get(todoId) != null) {
            todoList.remove(todoId);
            System.out.println("Todo with the id: " + todoId + " was removed.");
        } else {
            System.out.println("There isn't any todo under id: " + todoId);
        }
    }

    @Override
    public void saveSteps(Long todoId, List<Step> createdSteps) {
        var existingTodo = todoList.get(todoId);
        if (existingTodo != null) {
            System.out.println("Size of step array before adding new: " + existingTodo.steps().size());
            for (Step step : createdSteps) {
                if (canAddStepToTodo(existingTodo)) {
                    Step createdStep = createNewStepForTodo(existingTodo, step);
                    existingTodo.steps().add(createdStep);
                } else {
                    System.out.println("You have reached the maximum number of steps of 10 for todo with id: " + existingTodo.id());
                    break;
                }
            }
            System.out.println("Size of step array after adding new: " + existingTodo.steps().size());
        } else {
            System.out.println("Such a Todo does not exist.");
        }
    }

    @Override
    public void updateStep(Long todoId, int oldStepId, Step updatedStep) {
        var existingTodo = todoList.get(todoId);
        if (existingTodo != null) {
            for (int i = 0; i < existingTodo.steps().size(); i++) {
                if (existingTodo.steps().get(i).id() == oldStepId) {
                    existingTodo.steps().set(i, createNewStepId(((long) oldStepId), updatedStep));
                    System.out.println("Step with id:" + oldStepId + " has been updated");
                    return;
                }
            }
            System.out.println("Step with id:" + oldStepId + " does not exist");
        } else {
            System.out.println("Todo with id:" + todoId + " does not exist");
        }
    }

    @Override
    public void deleteSteps(Long todoId, List<Long> stepIds) {
        var existingTodo = todoList.get(todoId);
        if (existingTodo != null && stepIds.size() > 0 && !stepIds.contains(0L)) {
            System.out.println("Size of step array before removing step: " + existingTodo.steps().size());
            Iterator<Step> iterator = existingTodo.steps().iterator();
            while (iterator.hasNext()) {
                Step step = iterator.next();
                if (stepIds.contains(step.id())) {
                    iterator.remove();
                    System.out.println("Step with id:" + step.id() + " was removed.");
                }
            }
            System.out.println("Size of step array after removing step: " + existingTodo.steps().size());
        } else {
            System.out.println("No such todo with this id exists.");
        }
    }


    private boolean canAddStepToTodo(Todo todo) {
        return todo.steps().size() < MAX_NUM_STEPS;
    }

    private Step createNewStepForTodo(Todo todo, Step step) {
        Long newStepId = (long) (todo.steps().size() + 1);
        return new Step(newStepId, step.name(), step.description());
    }

    private Step createNewStepId(Long id, Step step) {
        return new Step(id, step.name(), step.description());
    }

    private Todo createNewTodoId(Long id, Todo todo) {
        return new Todo(id, todo.name(), todo.description(), todo.priority(), todo.steps());
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
