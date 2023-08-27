package com.jakub.sandbox;

import com.jakub.todoSandbox.model.Priority;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import com.jakub.todoSandbox.repository.TodoRepository;
import com.jakub.todoSandbox.service.TodoService;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

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
        Todo todoMock = new Todo(todoId, "Todo", "Description", Priority.LOW, steps);
        List<Step> addSteps = new ArrayList<>();

        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(todoMock));
        assertDoesNotThrow(() -> todoService.canAddStepsToTodo(todoMock.id(), addSteps));
    }

    @Test
    void canAddStepsToTodo_NullTodo() {
        long todoId = 1L;
        List<Step> steps = new ArrayList<>();

        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.empty());
        assertThrows(ValidationException.class, () -> todoService.canAddStepsToTodo(todoId, steps));
    }

    @Test
    void canAddStepsToTodo_ExceededNumberOfSteps() {
        long todoId = 1L;
        List<Step> steps = new ArrayList<>();

        Todo todoMock = new Todo(todoId, "Todo", "Description", Priority.LOW, steps);
        Step step = mock(Step.class);
        List<Step> addSteps = new ArrayList<>();
        int MAX_NUM_OF_STEPS = 10;
        for (int i = 0; i < MAX_NUM_OF_STEPS + 1; i++) {
            addSteps.add(step);
        }
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(todoMock));
        assertThrows(ValidationException.class, () -> todoService.canAddStepsToTodo(todoMock.id(), addSteps));
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

    @Test
    void findAllTodos_ValidTodoList() {
        List<Todo> mockTodoList = new ArrayList<Todo>();
        List<Step> steps = new ArrayList<>();
        Todo todo1 = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);
        Todo todo2 = new Todo(2L, "Todo2", "Description2", Priority.HIGH, steps);
        mockTodoList.add(todo1);
        mockTodoList.add(todo2);

        when(todoRepositoryMock.findAllTodos()).thenReturn(mockTodoList);
        List<Todo> results = todoService.findAllTodos();

        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(2, results.size());
    }

    @Test
    void findAllTodos_EmptyTodoList() {
        when(todoRepositoryMock.findAllTodos()).thenReturn(Collections.emptyList());
        List<Todo> results = todoService.findAllTodos();

        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void saveTodo_ValidTodoWithSteps() {
        List<Step> steps = new ArrayList<>();
        Step mockStep1 = new Step(1L, "Step1", "Desc1");
        Step mockStep2 = new Step(1L, "Step2", "Desc2");
        steps.add(mockStep1);
        steps.add(mockStep2);
        Todo validTodo = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);

        when(todoRepositoryMock.saveTodo(any(Todo.class))).thenReturn(validTodo);
        Todo result = todoService.saveTodo(validTodo);

        verify(todoRepositoryMock).saveTodo(validTodo);

        assertNotNull(result);
        assertEquals("Todo1", validTodo.name());
        assertEquals("Description1", validTodo.description());
        assertEquals(Priority.LOW, validTodo.priority());
        assertEquals(2,validTodo.steps().size());
        assertEquals("Step1", validTodo.steps().get(0).name());
        assertEquals("Desc1", validTodo.steps().get(0).description());
        assertEquals("Step2", validTodo.steps().get(1).name());
        assertEquals("Desc2", validTodo.steps().get(1).description());
    }

    @Test
    void saveTodo_ValidTodoWithoutSteps() {
        List<Step> steps = new ArrayList<>();

        Todo validTodo = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);

        when(todoRepositoryMock.saveTodo(any(Todo.class))).thenReturn(validTodo);
        Todo result = todoService.saveTodo(validTodo);

        verify(todoRepositoryMock).saveTodo(validTodo);

        assertNotNull(result);
        assertEquals("Todo1", validTodo.name());
        assertEquals("Description1", validTodo.description());
        assertEquals(Priority.LOW, validTodo.priority());
        assertEquals(0,validTodo.steps().size());
    }

    @Test
    void saveTodo_InvalidTodo() {
        List<Step> steps = new ArrayList<>();
        Todo invalidTodo = new Todo(1L, "A".repeat(101), "Description1", Priority.LOW, steps);

        assertThrows(ValidationException.class, () -> todoService.saveTodo(invalidTodo));
        verify(todoRepositoryMock, never()).saveTodo(any());
    }

    @Test
    void updateTodo_ValidTodo() {

    }
}
