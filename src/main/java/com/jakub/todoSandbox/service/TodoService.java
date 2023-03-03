package com.jakub.todoSandbox.service;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TodoService {

    // Temporary Map for todos - for testing purposes TODO implement postgress database for todos and steps instead of map
    private Map<Long, Todo> todoList;

    public TodoService(Map<Long, Todo> todoList) {
        this.todoList = todoList;
    }

    public ModelAndView isListEmpty() {
        if(todoList.isEmpty()) {
            System.out.println("List is empty, returned to index.html");
            return new ModelAndView("index.html");
        } else {
            System.out.println("List is not empty, forwarding to todo-list");
            return new ModelAndView("/todo-list");
        }
    }

    public void createTodo(Todo todo) {
        todo.setId(todoList.size() + 1L);
        todoList.put(todo.getId(),todo);
        System.out.println(validateNameAndDesc(todo.getName(), todo.getDescription()));
        System.out.println(
                "New todo was created: \n name: " + todo.getName()
                + "\n description: " + todo.getDescription()
                + "\n priority: " + todo.getPriority()
                + "\n steps: \n" + todo.getNestedSteps().get(0).getName());
    }

    // Update only the main to-do, not its steps
    public void updateTodo(Long id, Todo todo) {
        Todo exists = todoList.get(id);
        if(exists != null) {
            exists.setName(todo.getName());
            exists.setDescription(todo.getDescription());
            exists.setPriority(todo.getPriority());
            todoList.put(id, exists);
            System.out.println("Todo using id: {" + id +"} has been updated.");
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

    public void createStep(Long todoId, Step createdStep) {
        Todo exists = todoList.get(todoId);
        if (exists != null && (exists.getNestedSteps().size() < 10)) {
            System.out.println("Size of step array before adding new: " + exists.getNestedSteps().size());
            createdStep.setId((long) (exists.getNestedSteps().size() + 1));
            exists.getNestedSteps().add(createdStep);
            System.out.println("Size of step array after adding new: " + exists.getNestedSteps().size());
        } else {
            System.out.println("Exceeded maximum number of steps of 10 for todo, or todo does not exist.");
        }
    }

    public void deleteStep(Long todoId, int stepId) {
        Todo exists = todoList.get(todoId);
        if(exists != null && stepId >= 0) {
            for(Step step : exists.getNestedSteps()) {
                if (step.getId() == stepId) {
                    System.out.println("Size of step array before removing step: " + exists.getNestedSteps().size());
                    exists.getNestedSteps().remove(step);
                    System.out.println("Step with id:" + stepId + " was removed.");
                    System.out.println("Size of step array after removing step: " + exists.getNestedSteps().size());
                    break;
                }
            }

        } else {
            System.out.println("No such todo with this id exists.");
        }
    }

    public void updateStep(Long todoId, int oldStepId, Step updatedStep) {
        Todo exists = todoList.get(todoId);
        if (exists != null && oldStepId >= 0) {
            for (Step step : exists.getNestedSteps()) {
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
        if(!name.isEmpty() && !name.trim().isEmpty() && name.length() <= 100 && description.length() < 3000) {
            boolean hasAlpha = name.matches("^.*[^a-zA-Z0-9 ].*$");
            if(!hasAlpha) {
                System.out.println("Name only has alphanumeric");
                return true;
            }
        }else
            System.out.println("Has non alphanumeric symbol or name is null");
        return false;
    }

    private List<Todo> sortByPriority(List<Todo> todos) {
        Comparator<Todo> priorotyComparator = Comparator.comparing(Todo::getPriority);
        return todos.stream()
                .sorted(priorotyComparator)
                .collect(Collectors.toList());
    }
}
