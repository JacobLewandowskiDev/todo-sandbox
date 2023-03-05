package com.jakub.todoSandbox.controller;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.service.TodoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

@RestController
@RequestMapping("/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping("/")
    public String getCreateTodoView() {
        return todoService.isListEmpty();
    }

    @GetMapping("/get-todo")
    public ResponseEntity<Todo> getTodo(@RequestParam("id") Long id) {
        return new ResponseEntity<Todo>(todoService.getTodoById(id), HttpStatus.OK);
    }

    @GetMapping("/todo-list")
    public ResponseEntity<List<Todo>> getAllTodos() {
        return new ResponseEntity<List<Todo>>(todoService.getTodoList(), HttpStatus.OK);
    }

    @PostMapping (value = "/create-todo",  consumes = "application/json")
    public void createTodo(@RequestBody Todo todo) {
        getCreateTodoView();
        todoService.createTodo(todo);
    }

    @DeleteMapping("/delete-todo")
    public void deleteTodo(@RequestParam("id") Long id) {
        todoService.deleteTodo(id);
    }

    @PutMapping("/update-todo")
    public void updateTodo(@RequestParam("id") Long id, @RequestBody Todo todo) {
        todoService.updateTodo(id, todo);
    }

    //Step mappings
    @PostMapping("/add-step")
    public void createStep(@RequestParam("id") Long id, @RequestBody Step createdStep) {
        todoService.createStep(id, createdStep);
    }

    @DeleteMapping("/delete-step")
    public void deleteStep(@RequestParam("id") Long id, @RequestParam("step_id") int step_id) {
        todoService.deleteStep(id, step_id);
    }

    @PutMapping("/update-step")
    public void updateStep(@RequestParam("id") Long id, @RequestParam("step_id") int step_id, @RequestBody Step updatedStep) {
        todoService.updateStep(id, step_id, updatedStep);
    }


}
