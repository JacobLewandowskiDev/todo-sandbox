package com.jakub.sandbox;

import com.jakub.todoSandbox.model.Priority;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import com.jakub.todoSandbox.repository.TodoRepository;
import com.jakub.todoSandbox.service.TodoService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class TodoServiceTest {

    private final TodoRepository todoRepositoryMock = mock(TodoRepository.class);
    private final TodoService todoService = new TodoService(todoRepositoryMock);

    @Test
    void validateNameAndDesc_CorrectNameAndDesc() {
        assertDoesNotThrow(() -> todoService.validateNameAndDesc("A correct name for a todo", "This is a correct description for a todo"));
    }

    @Test
    void validateNameAndDesc_NullName() {
        assertThrows(ValidationException.class, () -> todoService.validateNameAndDesc(null, "This is a correct description for a todo"));
    }

    @Test
    void validateNameAndDesc_BlankName() {
        assertThrows(ValidationException.class, () -> todoService.validateNameAndDesc("", "This is a correct description for a todo"));
    }

    @Test
    void validateNameAndDesc_ExceededNameLength() {
        assertThrows(ValidationException.class, () -> todoService.validateNameAndDesc("A".repeat(101), "This is a correct description for a todo"));
    }

    @Test
    void validateNameAndDesc_InvalidName() {
        assertThrows(ValidationException.class, () -> todoService.validateNameAndDesc("Invalid_name!", "This is a correct description for a todo"));
    }

    @Test
    void validateNameAndDesc_ExceededDescLength() {
        assertThrows(ValidationException.class, () -> todoService.validateNameAndDesc("Correct Name", "A".repeat(3000)));
    }

    @Test
    void canAddStepsToTodo_ValidSteps() {
        long todoId = 1L;
        List<Step> steps = new ArrayList<>();
        Todo todoMock = new Todo(todoId,"Todo", "Description", Priority.LOW, steps);
        List<Step> addSteps = new ArrayList<>();
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(todoMock));
        assertDoesNotThrow(() -> todoService.canAddStepsToTodo(todoMock.id(),addSteps));
    }

    @Test
    void canAddStepsToTodo_NullTodo() {
        long todoId = 1L;
        List<Step> steps = new ArrayList<>();

        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> todoService.canAddStepsToTodo(todoId,steps));
    }

    @Test
    void canAddStepsToTodo_ExceededNumberOfSteps() {
        long todoId = 1L;
        List<Step> steps = new ArrayList<>();

        Todo todoMock = new Todo(todoId,"Todo", "Description", Priority.LOW, steps);
        Step step = mock(Step.class);
        List<Step> addSteps = new ArrayList<>();
        int MAX_NUM_OF_STEPS = 10;
        for (int i = 0; i < MAX_NUM_OF_STEPS + 1; i++) {
            addSteps.add(step);
        }
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(todoMock));
        assertThrows(ValidationException.class, () -> todoService.canAddStepsToTodo(todoMock.id(),addSteps));
    }

    @Test
    void findTodoById_ValidTodoId() {
        long todoId = 1L;
        List<Step> steps = new ArrayList<>();
        Todo todoMock = new Todo(todoId, "Todo", "Description", Priority.HIGH, steps);
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(todoMock));
        assertDoesNotThrow(() -> todoService.findTodoById(todoId));
    }

    @Test
    void findTodoById_InvalidTodoId() {
        long todoId = 1L;
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.empty());
        Optional<Todo> result = todoService.findTodoById(todoId);
        assertTrue(result.isEmpty());
    }
}
