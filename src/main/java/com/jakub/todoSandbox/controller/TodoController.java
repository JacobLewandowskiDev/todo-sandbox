package com.jakub.todoSandbox.controller;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
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
    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        System.out.println(new ResponseEntity<List<Todo>>(todoService.findAllTodos(), HttpStatus.OK));
        return new ResponseEntity<List<Todo>>(todoService.findAllTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Optional<Todo> getTodoById(@PathVariable("id") long id) {
        return todoService.findTodoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo saveTodo(@RequestBody Todo todo) {
        return todoService.saveTodo(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable("id") long id) {
        todoService.deleteTodo(id);
    }

    @PutMapping("/{id}")
    public void updateTodo(@PathVariable("id") long id, @RequestBody Todo todo) {
        todoService.updateTodo(id, todo);
    }

    @PostMapping("/{id}/steps")
    @ResponseStatus(HttpStatus.CREATED)
    public void saveSteps(@PathVariable("id") long id, @RequestBody List<Step> steps) {
        todoService.saveSteps(id, steps);
    }

    @DeleteMapping("/{id}/steps")
    public void deleteStep(@PathVariable("id") long id, @RequestParam("stepId") List<Long> stepId) {
        todoService.deleteSteps(id, stepId);
    }

    @PutMapping("/{id}/steps")
    public void updateStep(@PathVariable("id") long id, @RequestBody Step updatedStep) {
        todoService.updateStep(id, updatedStep);
    }
}
