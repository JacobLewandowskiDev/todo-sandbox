package com.jakub.todoSandbox.controller;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.repository.TodoRepository;
import com.jakub.todoSandbox.service.TodoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/todos")
public class TodoController {

    @Autowired
    private final TodoRepository todoRepository;
    private final TodoService todoService;

    public TodoController(TodoRepository todoRepository, TodoService todoService) {
        this.todoRepository = todoRepository;
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return new ResponseEntity<List<Todo>>(todoRepository.findAllTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Optional<Todo> getTodoById(@PathVariable("id") long id) {
        return todoRepository.findTodoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo saveTodo(@RequestBody Todo todo) {
        todoService.validateNameAndDesc(todo.name(), todo.description());
        if(!todo.steps().isEmpty()) {
            for (Step step : todo.steps()) {
                todoService.validateNameAndDesc(step.name(), step.description());
            }
        }
        return todoRepository.saveTodo(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable("id") long id) {
        todoRepository.deleteTodo(id);
    }

    @PutMapping("/{id}")
    public void updateTodo(@PathVariable("id") long id, @RequestBody Todo todo) {
        todoRepository.updateTodo(id, todo);
    }

    @PostMapping("/{id}/steps")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveSteps(@PathVariable("id") long id, @RequestBody List<Step> steps) {
        for (Step step : steps) {
            todoService.validateNameAndDesc(step.name(), step.description());
        }
        todoService.canAddStepsToTodo(id, steps);
        todoRepository.saveSteps(id, steps);
    }

    @DeleteMapping("/{id}/steps")
    public void deleteStep(@PathVariable("id") long id, @RequestParam("stepId") List<Long> stepId) {
        todoRepository.deleteSteps(id, stepId);
    }

    @PutMapping("/{id}/steps")
    public void updateStep(@PathVariable("id") long id, @RequestBody Step updatedStep) {
        todoService.validateNameAndDesc(updatedStep.name(), updatedStep.description());
        todoRepository.updateStep(id, updatedStep);
    }


}
