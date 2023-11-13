package com.jakub.todoSandbox;

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
        //Given
        long todoId = 1L;
        Todo existingTodo = new Todo(todoId, "Todo", "Description", Priority.LOW, new ArrayList<>());
        List<Step> addSteps = new ArrayList<>();

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));

        //Assert
        assertDoesNotThrow(() -> todoService.canAddStepsToTodo(existingTodo.id(), addSteps));
    }

    @Test
    void canAddStepsToTodo_NullTodo() {
        //Given
        long todoId = 1L;

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.empty());

        //Assert
        assertThrows(ValidationException.class, () -> todoService.canAddStepsToTodo(todoId, new ArrayList<>()));
    }

    @Test
    void canAddStepsToTodo_ExceededNumberOfSteps() {
        //Given
        long todoId = 1L;
        List<Step> steps = new ArrayList<>();
        Todo existingTodo = new Todo(todoId, "Todo", "Description", Priority.LOW, steps);
        Step step = mock(Step.class);
        List<Step> addSteps = new ArrayList<>();
        int MAX_NUM_OF_STEPS = 10;
        for (int i = 0; i < MAX_NUM_OF_STEPS + 1; i++) {
            addSteps.add(step);
        }

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));
        //Assert

        assertThrows(ValidationException.class, () -> todoService.canAddStepsToTodo(existingTodo.id(), addSteps));
    }

    @Test
    void findTodoById_ValidTodoId() {
        //Given
        long todoId = 1L;
        Todo existingTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, new ArrayList<>());

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));

        //Assert
        assertDoesNotThrow(() -> todoService.findTodoById(todoId));
    }

    @Test
    void findTodoById_InvalidTodoId() {
        //Given
        long todoId = 1L;

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.empty());
        Optional<Todo> result = todoService.findTodoById(todoId);

        //Assert
        assertTrue(result.isEmpty());
    }

    @Test
    void findAllTodos_ValidTodoList() {
        //Given
        List<Todo> mockTodoList = new ArrayList<Todo>();
        Todo todo1 = new Todo(1L, "Todo1", "Description1", Priority.LOW, new ArrayList<>());
        Todo todo2 = new Todo(2L, "Todo2", "Description2", Priority.HIGH, new ArrayList<>());
        mockTodoList.add(todo1);
        mockTodoList.add(todo2);

        //When
        when(todoRepositoryMock.findAllTodos()).thenReturn(mockTodoList);
        List<Todo> results = todoService.findAllTodos();

        //Assert
        assertNotNull(results);
        assertFalse(results.isEmpty());
        assertEquals(2, results.size());
    }

    @Test
    void findAllTodos_EmptyTodoList() {
        //When
        when(todoRepositoryMock.findAllTodos()).thenReturn(Collections.emptyList());
        List<Todo> results = todoService.findAllTodos();

        //Assert
        assertNotNull(results);
        assertTrue(results.isEmpty());
    }

    @Test
    void saveTodo_ValidTodoWithSteps() {
        //Given
        List<Step> steps = new ArrayList<>();
        Step step1 = new Step(1L, "Step1", "Desc1");
        Step step2 = new Step(1L, "Step2", "Desc2");
        steps.add(step1);
        steps.add(step2);
        Todo validTodo = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);

        //When
        when(todoRepositoryMock.saveTodo(any(Todo.class))).thenReturn(validTodo);
        Todo result = todoService.saveTodo(validTodo);

        //Assert
        verify(todoRepositoryMock).saveTodo(validTodo);

        assertNotNull(result);
        assertEquals("Todo1", validTodo.name());
        assertEquals("Description1", validTodo.description());
        assertEquals(Priority.LOW, validTodo.priority());
        assertEquals(2, validTodo.steps().size());
        assertEquals("Step1", validTodo.steps().get(0).name());
        assertEquals("Desc1", validTodo.steps().get(0).description());
        assertEquals("Step2", validTodo.steps().get(1).name());
        assertEquals("Desc2", validTodo.steps().get(1).description());
    }

    @Test
    void saveTodo_ValidTodoWithoutSteps() {
        //Given
        List<Step> steps = new ArrayList<>();
        Todo validTodo = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);

        //When
        when(todoRepositoryMock.saveTodo(any(Todo.class))).thenReturn(validTodo);
        Todo result = todoService.saveTodo(validTodo);

        //Assert
        verify(todoRepositoryMock).saveTodo(validTodo);

        assertNotNull(result);
        assertEquals("Todo1", validTodo.name());
        assertEquals("Description1", validTodo.description());
        assertEquals(Priority.LOW, validTodo.priority());
        assertEquals(0, validTodo.steps().size());
    }

    @Test
    void saveTodo_InvalidTodo() {
        //Given
        Todo invalidTodo = new Todo(1L, "A".repeat(101), "Description1", Priority.LOW, new ArrayList<>());

        //Assert
        assertThrows(ValidationException.class, () -> todoService.saveTodo(invalidTodo));
        verify(todoRepositoryMock, never()).saveTodo(any());
    }

    @Test
    void updateTodo_ValidTodo() {
        //Given
        long todoId = 1L;
        Todo existingTodo = new Todo(todoId, "Valid Todo", "Description", Priority.LOW, new ArrayList<>());
        Todo updatedTodo = new Todo(todoId, "New Todo name", "New Description", Priority.HIGH, new ArrayList<>());

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));
        doNothing().when(todoRepositoryMock).updateTodo(todoId, updatedTodo);

        //Assert
        assertDoesNotThrow(() -> todoService.updateTodo(todoId, updatedTodo));
        verify(todoRepositoryMock).updateTodo(todoId, updatedTodo);
    }

    @Test
    void updateTodo_InvalidTodo() {
        //Given
        long todoId = 1L;
        Todo updatedTodo = new Todo(todoId, "New Todo name", "New Description", Priority.HIGH, new ArrayList<>());

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.empty());

        //Assert
        assertThrows(ValidationException.class, () -> todoService.updateTodo(todoId, updatedTodo));
        verify(todoRepositoryMock, never()).updateTodo(anyLong(), any(Todo.class));
    }

    @Test
    void deleteTodo() {
        //Given
        long todoId = 1L;
        Todo existingTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, new ArrayList<>());

        //When
        when(todoRepositoryMock.deleteTodo(todoId)).thenReturn(Optional.of(existingTodo));

        //Assert
        assertEquals(Optional.of(existingTodo), todoService.deleteTodo(todoId));
        verify(todoRepositoryMock).deleteTodo(todoId);
    }


    @Test
    void saveSteps_ValidSteps() {
        //Given
        long todoId = 1L;
        Todo existingTodo = mock(Todo.class);
        List<Step> stepsToBeSaved = new ArrayList<>();
        int MAX_NUM_OF_STEPS = 10;
        long stepId = 0L;
        for (int i = 0; i < MAX_NUM_OF_STEPS; i++) {
            stepId += 1L;
            Step step = new Step(stepId, "step" + i, "Desc" + i);
            stepsToBeSaved.add(step);
        }

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));
        todoService.saveSteps(todoId, stepsToBeSaved);

        //Assert
        verify(todoRepositoryMock).saveSteps(todoId, stepsToBeSaved);
    }

    @Test
    void saveSteps_InvalidSteps() {
        //Given
        long todoId = 1L;
        Todo existingTodo = mock(Todo.class);
        List<Step> stepsToBeSaved = new ArrayList<>();
        Step invalidStep = new Step(1L, "Invalid_StepName!@", "Description");
        stepsToBeSaved.add(invalidStep);

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));

        //Assert
        assertThrows(ValidationException.class, () -> todoService.saveSteps(todoId, stepsToBeSaved));
        verify(todoRepositoryMock, never()).saveSteps(todoId, stepsToBeSaved);
    }

    @Test
    void saveSteps_ExceededNumberOfSteps() {
        //Given
        long todoId = 1L;
        Todo existingTodo = mock(Todo.class);
        List<Step> stepsToBeSaved = new ArrayList<>();
        int MAX_NUM_OF_STEPS = 10;
        for (int i = 0; i <= MAX_NUM_OF_STEPS; i++) {
            Step mockStep = mock(Step.class);
            stepsToBeSaved.add(mockStep);
        }

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));

        //Assert
        assertThrows(ValidationException.class, () -> todoService.saveSteps(todoId, stepsToBeSaved));
        verify(todoRepositoryMock, never()).saveSteps(todoId, stepsToBeSaved);
    }

    @Test
    void updateStep_ValidStep() {
        //Given
        long todoId = 1L;
        List<Step> existingSteps = new ArrayList<>();
        Step existingStep = new Step(1L, "Old Step name", "Old step description");
        Step updatedStep = new Step(1L, "Updated Step name", "Updated step description");
        existingSteps.add(existingStep);
        Todo existingTodo = new Todo(todoId, "Name", "Description", Priority.LOW, existingSteps);

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));
        doNothing().when(todoRepositoryMock).updateStep(todoId, updatedStep);

        //Assert
        assertDoesNotThrow(() -> todoService.updateStep(todoId, updatedStep));
        verify(todoRepositoryMock, times(1)).updateStep(todoId, updatedStep);
    }

    @Test
    void updateStep_TodoNotFound() {
        //Given
        long todoId = 1L;
        Step updatedStep = new Step(1L, "Updated Step name", "Updated step description");

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.empty());

        //Assert
        assertThrows(ValidationException.class, () -> todoService.updateStep(todoId, updatedStep));
        verify(todoRepositoryMock, never()).updateStep(anyLong(), any(Step.class));
    }

    @Test
    void updateStep_StepNotFoundInTodo() {
        //Given
        long todoId = 1L;
        Step existingStep = new Step(1L, "Updated Step name", "Updated step description");
        Step updatedStep = new Step(2L, "Updated Step name", "Updated step description");
        List<Step> steps = new ArrayList<>();
        steps.add(existingStep);
        Todo existingTodo = new Todo(todoId, "Name", "Description", Priority.LOW, steps);

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));

        //Assert
        assertThrows(ValidationException.class, () -> todoService.updateStep(todoId, updatedStep));
        verify(todoRepositoryMock, never()).updateStep(todoId, updatedStep);
    }

    @Test
    void deleteSteps() {
        //Given
        long todoId = 1L;
        List<Long> stepIds = List.of(1L, 2L);
        Todo existingTodo = new Todo(
                todoId, "Todo", "Description", Priority.HIGH,
                List.of(new Step(1L, "Name 1", "Description"), new Step(2L, "Name 2", "Description"))
        );

        //When
        when(todoRepositoryMock.findTodoById(todoId)).thenReturn(Optional.of(existingTodo));
        todoService.deleteSteps(todoId, stepIds);

        //Assert
        verify(todoRepositoryMock).deleteSteps(todoId, stepIds);
    }
}
