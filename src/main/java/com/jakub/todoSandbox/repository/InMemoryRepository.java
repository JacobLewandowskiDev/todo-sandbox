package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import com.jakub.todoSandbox.service.TodoService;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class InMemoryRepository implements TodoRepository {

    private final Map<Long, Todo> todos = new HashMap<Long, Todo>();
    private final TodoService todoService;
    private Long maxTodoId = 0L;

    public InMemoryRepository(TodoService todoService) {
        this.todoService = todoService;
    }

    @Override
    public Optional<Todo> findTodoById(Long todoId) {
        return Optional.ofNullable(todos.get(todoId));
    }

    @Override
    public List<Todo> findAllTodos() {
        List<Todo> todoMapToList = new ArrayList<>(todos.values());
        return todoService.sortByPriority(todoMapToList);
    }

    @Override
    public Todo saveTodo(Todo todo) throws ValidationException {
        if (todoService.validateNameAndDesc(todo.name(), todo.description())) {
            var toBeSavedTodo = new Todo((maxTodoId + 1L), todo.name(), todo.description(), todo.priority(), todo.steps());
            todos.put(toBeSavedTodo.id(), toBeSavedTodo);
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
            throw new ValidationException("The name, or description for the todo " + todo.name()
                    + "with id: :" + todo.id() + " did not pass the validation");
        }
    }

    @Override
    public void updateTodo(Long todoId, Todo todo) throws ValidationException {
        var existingTodo = todos.get(todoId);
        if (existingTodo != null) {
            if (todo.id() == null) {
                throw new ValidationException("Todo id cannot be null");
            }
            todos.put(todoId, todoService.createNewTodoId(todoId, todo));
            System.out.println("Todo using id: {" + todoId + "} has been updated.");
        } else {
            throw new ValidationException("No todo exists under id: " + todoId);
        }
    }

    @Override
    public Todo deleteTodo(Long todoId) {
        return todos.remove(todoId);
    }

    @Override
    public void saveSteps(Long todoId, List<Step> createdSteps) throws ValidationException {
        var existingTodo = todos.get(todoId);
        if (existingTodo != null) {
            System.out.println("Size of step array before adding new: " + existingTodo.steps().size());
            for (Step step : createdSteps) {
                if (todoService.canAddStepToTodo(existingTodo)) {
                    Step createdStep = todoService.createNewStepForTodo(existingTodo, step);
                    existingTodo.steps().add(createdStep);
                } else {
                    System.out.println("You have reached the maximum number of steps of 10 for todo with id: " + existingTodo.id());
                    break;
                }
            }
            System.out.println("Size of step array after adding new: " + existingTodo.steps().size());
        }
        throw new ValidationException("Creation of step was not possible, due to either non existing todo, " +
                "or the new steps have not passed the validation process.");
    }

    @Override
    public void updateStep(Long todoId, Step updatedStep) throws ValidationException {
        var existingTodo = todos.get(todoId);
        if (existingTodo != null) {
            List<Step> steps = existingTodo.steps();
            Optional<Step> stepToUpdate = steps.stream()
                    .filter(step -> Objects.equals(step.id(), updatedStep.id()))
                    .findFirst();
            if (stepToUpdate.isPresent()) {
                int index = steps.indexOf(stepToUpdate.get());
                steps.set(index, todoService.createNewStepId(((long) updatedStep.id()), updatedStep));
                System.out.println("Step with id:" + updatedStep.id() + " has been updated");
            }
        }
        throw new ValidationException("Update was not possible, due to either the step not existing or the Id was invalid");
    }

    @Override
    public void deleteSteps(Long todoId, List<Long> stepIds) {
        var existingTodo = todos.get(todoId);
        if (existingTodo != null) {
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

}
