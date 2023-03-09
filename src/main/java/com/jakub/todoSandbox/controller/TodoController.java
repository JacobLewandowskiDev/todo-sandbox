package com.jakub.todoSandbox.controller;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return new ResponseEntity<List<Todo>>(todoService.getTodoList(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Todo getTodo(@PathVariable("id") Long id) {
       return todoService.getTodoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodo(@RequestBody Todo todo) {
        return todoService.createTodo(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable("id") Long id) {
        todoService.deleteTodo(id);
    }

    @PutMapping("/{id}")
    public void updateTodo(@PathVariable("id") Long id, @RequestBody Todo todo) {
        todoService.updateTodo(id, todo);
    }

    //Step mappings
    @PostMapping("/{id}/steps")
    public void createStep(@PathVariable("id") Long id, @RequestBody Step createdStep) {
        todoService.createStep(id, createdStep);
    }

    @DeleteMapping("/{id}/steps")
    public void deleteStep(@PathVariable("id") Long id, @RequestParam("step_id") int step_id) {
        todoService.deleteStep(id, step_id);
    }

    @PutMapping("/{id}/steps")
    public void updateStep(@PathVariable("id") Long id, @RequestParam("step_id") int step_id, @RequestBody Step updatedStep) {
        todoService.updateStep(id, step_id, updatedStep);
    }


}
