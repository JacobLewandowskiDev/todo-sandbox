package com.jakub.todoSandbox.controller;

import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.repository.TodoRepository;
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

    public TodoController( TodoRepository todoRepository) {
        this.todoRepository = todoRepository;
    }

    @GetMapping
    public ResponseEntity<List<Todo>> getAllTodos() {
        return new ResponseEntity<List<Todo>>(todoRepository.findAllTodos(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public Optional<Todo> getTodo(@PathVariable("id") Long id) {
       return todoRepository.findTodoById(id);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Todo createTodo(@RequestBody Todo todo) {
        return todoRepository.saveTodo(todo);
    }

    @DeleteMapping("/{id}")
    public void deleteTodo(@PathVariable("id") Long id) {
        todoRepository.deleteTodo(id);
    }

    @PutMapping("/{id}")
    public void updateTodo(@PathVariable("id") Long id, @RequestBody Todo todo) {
        todoRepository.updateTodo(id, todo);
    }

    //Step mappings
    @PostMapping("/{id}/steps")
    @ResponseStatus(HttpStatus.CREATED)
    public void createStep(@PathVariable("id") Long id, @RequestBody List<Step> createdSteps) {
        todoRepository.saveSteps(id, createdSteps);
    }

    @DeleteMapping("/{id}/steps")
    public void deleteStep(@PathVariable("id") Long id, @RequestParam("step_ids") List<Long> step_ids) {
        todoRepository.deleteSteps(id, step_ids);
    }

    @PutMapping("/{id}/steps/{step_id}")
    public void updateStep(@PathVariable("id") Long id, @PathVariable("step_id") int step_id, @RequestBody Step updatedStep) {
        todoRepository.updateStep(id, step_id, updatedStep);
    }


}
