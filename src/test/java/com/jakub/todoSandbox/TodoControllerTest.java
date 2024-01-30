package com.jakub.todoSandbox;

import com.jakub.todoSandbox.model.Priority;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import com.jakub.todoSandbox.repository.TodoRepository;
import com.jakub.todoSandbox.service.TodoService;
import com.jakub.todoSandbox.support.IntegrationTest;
import com.jakub.todoSandbox.support.TestHttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;


public class TodoControllerTest extends IntegrationTest {

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
    void saveTodo_ValidTodoWithSteps() throws IOException {
        //Given
        List<Step> steps = new ArrayList<>();
        var step1 = new Step(1L, "Step1", "Desc1");
        var step2 = new Step(2L, "Step2", "Desc2");
        steps.add(step1);
        steps.add(step2);
        var todoId = 1L;
        var todo = new Todo(todoId, "Todo1", "Description1", Priority.LOW, steps);

        //When
        var saveTodoResponse = postResponse("todos", todo);
        var getTodoResponse = getByIdResponse(todoId);
        var responseBody = getTodoResponse.objectMapper().readValue(getTodoResponse.body(), Todo.class);

        //Assert
        assertResponseStatus(saveTodoResponse, HttpStatus.CREATED);
        var todoResponseBody = saveTodoResponse.objectMapper().readValue(saveTodoResponse.body(), Long.TYPE);

        assertNotNull(todoResponseBody);
        assertEquals(todoId, todoResponseBody);
        assertEquals(2, responseBody.steps().size());
    }

    @Test
    void saveTodo_ValidTodoWithoutSteps() throws IOException {
        //Given
        List<Step> steps = new ArrayList<>();
        var todoId = 1L;
        var todo = new Todo(todoId, "Todo1", "Description1", Priority.LOW, steps);

        //When
        var saveTodoResponse = postResponse("todos", todo);

        //Assert
        assertResponseStatus(saveTodoResponse, HttpStatus.CREATED);
        var saveResponseBody = saveTodoResponse.objectMapper().readValue(saveTodoResponse.body(), Long.TYPE);
        var getTodoResponse = getByIdResponse(todoId);
        var responseBody = getTodoResponse.objectMapper().readValue(getTodoResponse.body(), Todo.class);

        assertNotNull(saveResponseBody);
        assertEquals(todoId, saveResponseBody);
        assertEquals(0, responseBody.steps().size());
    }

    @Test
    void saveTodo_InvalidTodoName() {
        //Given
        var invalidTodo = new Todo(1L, "A".repeat(101), "Description1", Priority.LOW, new ArrayList<>());

        //When
        var saveTodoResponse = postResponse("todos", invalidTodo);

        //Assert
        assertResponseStatus(saveTodoResponse, HttpStatus.BAD_REQUEST);
    }

    @Test
    void findAllTodos_ValidTodoList() throws IOException {
        //Given
        var todo1 = new Todo(1L, "Todo1", "Description1", Priority.LOW, new ArrayList<>());
        var todo2 = new Todo(2L, "Todo2", "Description2", Priority.HIGH, new ArrayList<>());
        var todo1PostResponse = postResponse("todos", todo1);
        var todo2PostResponse = postResponse("todos", todo2);

        //When
        var responseBody = Arrays.asList(getResponse().objectMapper().readValue(getResponse().body(), Todo[].class));

        //Assert
        assertNotNull(responseBody);
        assertFalse(responseBody.isEmpty());
        assertEquals(2, responseBody.size());
        assertEquals(HttpStatus.OK.value() , getResponse().statusCode());
    }

    @Test
    void findAllTodos_EmptyTodoList() throws IOException {
        //Given nothing (empty list)

        //When
        var responseBody = Arrays.asList(getResponse().objectMapper().readValue(getResponse().body(), Todo[].class));

        //Assert
        assertNotNull(responseBody);
        assertTrue(responseBody.isEmpty());
        assertEquals(0, responseBody.size());
        assertEquals(HttpStatus.OK.value() , getResponse().statusCode());
    }

    @Test
    void findTodoById_ValidTodoId() throws IOException {
        //Given
        var todoId = 1L;
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, new ArrayList<>());
        var postTodo = postResponse("todos", validTodo);

        //When
        var getTodoByIdResponse = getByIdResponse(todoId);
        var responseBody = getTodoByIdResponse.objectMapper().readValue(getTodoByIdResponse.body(), Todo.class);
        System.out.println(responseBody);

        //Assert
        assertNotNull(responseBody);
        assertEquals(1L, responseBody.id());
        assertEquals("Todo", responseBody.name());
        assertEquals("Description", responseBody.description());
        assertEquals(Priority.HIGH, responseBody.priority());
        assertNotNull(responseBody.steps());
        assertEquals(0, responseBody.steps().size());
        assertEquals(HttpStatus.OK.value() ,getTodoByIdResponse.statusCode());
    }

    @Test
    void findTodoById_InvalidTodoId() throws IOException {
        //Given
        var todoId = 1L;
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, new ArrayList<>());
        var postTodo = postResponse("todos", validTodo);

        //When
        var getTodoByIdResponse = getByIdResponse(2L);
        var responseBody = getTodoByIdResponse.objectMapper().readValue(getTodoByIdResponse.body(), Todo.class);
        System.out.println(responseBody);

        //Assert
        assertNull(responseBody);
        assertEquals(HttpStatus.OK.value() ,getTodoByIdResponse.statusCode());
    }

    @Test
    void updateTodo_ValidTodo() throws IOException{
        //Given
        var todoId = 1L;
        var savedTodo = new Todo(todoId, "Valid Todo", "Description", Priority.LOW, new ArrayList<>());
        var updatedTodo = new Todo(0, "New Todo name", "New Description", Priority.HIGH, new ArrayList<>());

        //When
        var postTodoResponse = postResponse("todos", savedTodo);
        var updateResponse = updateResponse(todoId, updatedTodo);
        var getResponse = getByIdResponse(todoId);
        var responseBody = getResponse.objectMapper().readValue(getResponse.body(), Todo.class);
        System.out.println(responseBody);

        //Assert
        assertResponseStatus(postTodoResponse, HttpStatus.CREATED);
        assertResponseStatus(updateResponse, HttpStatus.OK);
        assertEquals("New Todo name", responseBody.name());
        assertEquals("New Description", responseBody.description());
        assertEquals(Priority.HIGH, responseBody.priority());
        assertEquals(0, responseBody.steps().size());
    }

    @Test
    void updateTodo_InvalidTodo() throws IOException{
        //Given
        var todoId = 1L;
        var updatedTodo = new Todo(0, "New Todo name", "New Description", Priority.HIGH, new ArrayList<>());

        //When
        var updateResponse = updateResponse(todoId, updatedTodo);
        var getResponse = getByIdResponse(todoId);
        var responseBody = getResponse.objectMapper().readValue(getResponse.body(), Todo.class);
        System.out.println(responseBody);

        //Assert
        assertResponseStatus(updateResponse, HttpStatus.BAD_REQUEST);
        assertNull(responseBody);
    }


    @Test
    void deleteTodo() {
        //Given
        var todoId = 1L;
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, new ArrayList<>());
        var postTodo = postResponse("todos", validTodo);

        //When
        var deleteTodoResponse = deleteResponse(todoId);

        //Assert
        assertEquals(HttpStatus.OK.value(), deleteTodoResponse.statusCode());
    }

    @Test
    void saveSteps_ValidSteps() throws IOException{
        //Given
        var todoId = 1L;
        List<Step> steps = new ArrayList<>();
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, steps);
        var postResponse = postResponse("todos", validTodo);

        var step = new Step(0L, "Step", "Step Description");
        steps.add(step);

        //When
        var statusCode = 201;
        while (statusCode == HttpStatus.CREATED.value()) {
            var postStepResponse = postStepResponse(todoId, steps);
            statusCode = postStepResponse.statusCode();
        }

        var getResponse = getByIdResponse(1L);
        var responseBody = getResponse.objectMapper().readValue(getResponse.body(), Todo.class);
        System.out.println("The Todos step list size is: " + responseBody.steps().size());

        //Assert
        assertResponseStatus(getResponse, HttpStatus.OK);
        assertNotNull(getResponse.body());
        assertNotNull(responseBody.steps());
        assertEquals(10, responseBody.steps().size()); //Assert that a TODOS step list cannot exceed size of 10.
    }

    @Test
    void saveSteps_NonExistingTodo() throws IOException {
        //Given
        List<Step> steps = new ArrayList<>();
        var step = new Step(0L, "Step", "Step Description");
        steps.add(step);

        //When
        var postStepResponse = postStepResponse(1L, steps);
        var responseBody = postStepResponse.objectMapper().readValue(postStepResponse.body(), Todo.class);

        //Assert
        assertResponseStatus(postStepResponse, HttpStatus.BAD_REQUEST);
        assertEquals(0, responseBody.id());
        assertNull(responseBody.name());
        assertNull(responseBody.steps());
    }

    @Test
    void saveSteps_InvalidSteps() throws IOException {
        //Given
        var todoId = 1L;
        List<Step> steps = new ArrayList<>();
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, steps);
        var postResponse = postResponse("todos", validTodo);

        var step = new Step(1L, "Invalid_StepName!@", "Description");
        steps.add(step);

        //When
        var postStepResponse = postStepResponse(todoId, steps);
        var getResponse = getByIdResponse(1L);
        var responseBody = getResponse.objectMapper().readValue(getResponse.body(), Todo.class);
        System.out.println("The Todos step list size is: " + responseBody.steps().size());

        //Assert
        assertResponseStatus(postStepResponse, HttpStatus.BAD_REQUEST);
        assertNotNull(responseBody);
        assertEquals(0, responseBody.steps().size());
    }

    @Test
    void updateStep_ValidStep() throws IOException {
        //Given
        var todoId = 1L;
        List<Step> steps = new ArrayList<>();
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, steps);
        var postResponse = postResponse("todos", validTodo);

        var stepId = 1L;
        var oldStep = new Step(stepId, "Old Step name", "Old step description");
        steps.add(oldStep);

        var newStep = new Step(stepId, "Updated Step name", "Updated step description");

        //When
        var postStepResponse = postStepResponse(todoId, steps);
        var updateStepResponse = updateStepResponse(todoId, newStep);
        var getResponse = getByIdResponse(todoId);
        var responseBody = getResponse.objectMapper().readValue(getResponse.body(), Todo.class);

        //Assert
        assertResponseStatus(updateStepResponse, HttpStatus.OK);
        assertNotNull(responseBody);
        assertEquals("Updated Step name", responseBody.steps().get(0).name());
        assertEquals("Updated step description", responseBody.steps().get(0).description());
        assertEquals(1, responseBody.steps().size());
    }

    @Test
    void updateStep_TodoNotFound() throws IOException {
        //Given
        var todoId = 1L;
        var newStep = new Step(1L, "Updated Step name", "Updated step description");

        //When
        var updateResponse = updateStepResponse(todoId, newStep);
        var getResponse = getByIdResponse(todoId);
        var responseBody = getResponse.objectMapper().readValue(getResponse.body(), Todo.class);

        //Assert
        assertResponseStatus(updateResponse, HttpStatus.BAD_REQUEST);
        assertNull(responseBody);
    }

    @Test
    void updateStep_StepNotFound() throws IOException {
        //Given
        var todoId = 1L;
        List<Step> steps = new ArrayList<>();
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, steps);
        var postResponse = postResponse("todos", validTodo);

        var stepId = 1L;
        var updatedStep = new Step(stepId, "Updated Step name", "Updated step description");


        //When
        var updateStepResponse = updateStepResponse(todoId, updatedStep);
        var getResponse = getByIdResponse(todoId);
        var responseBody = getResponse.objectMapper().readValue(getResponse.body(), Todo.class);

        //Assert
        assertResponseStatus(updateStepResponse, HttpStatus.BAD_REQUEST);
        assertTrue(responseBody.steps().isEmpty());
    }

    @Test
    void deleteSteps() throws IOException{
        //Given
        long todoId = 1L;
        List<Long> stepIdsForDeletion = List.of(1L, 2L);
        var validTodo = new Todo(
                todoId, "Todo", "Description", Priority.HIGH,
                List.of(new Step(1L, "Name 1", "Description"), new Step(2L, "Name 2", "Description"))
        );

        var postResponse = postResponse("todos", validTodo);

        //When
        var deleteResponse = deleteStepResponse(todoId, stepIdsForDeletion);
        var getByIdResponse = getByIdResponse(todoId);
        var responseBody = getByIdResponse.objectMapper().readValue(getByIdResponse.body(), Todo.class);

        //Assert
        assertResponseStatus(deleteResponse, HttpStatus.OK);
        assertEquals(0, responseBody.steps().size());
    }




    private TestHttpResponse getResponse() {
        return testHttpClient.request()
                .path("/todos")
                .GET()
                .execute();
    }

    private TestHttpResponse getByIdResponse(long todoId) {
        return testHttpClient.request()
                .path("todos/" + todoId)
                .GET()
                .execute();
    }

    private TestHttpResponse postResponse(String urlPath, Object request) {
        return testHttpClient.request()
                .path(urlPath)
                .POST()
                .body(request)
                .execute();
    }

    private TestHttpResponse updateResponse(long todoId, Object request) {
        return testHttpClient.request()
                .path("todos/" + todoId)
                .PUT()
                .body(request)
                .execute();
    }

    private TestHttpResponse deleteResponse(long todoId) {
        return testHttpClient.request()
                .path("todos/" + todoId)
                .DELETE()
                .execute();
    }

    private TestHttpResponse postStepResponse(long todoId, List<Step> request) {
        return testHttpClient.request()
                .path("todos/" + todoId + "/steps")
                .POST()
                .body(request)
                .execute();
    }

    private TestHttpResponse updateStepResponse(long todoId, Object request) {
        return testHttpClient.request()
                .path("todos/" + todoId + "/steps")
                .PUT()
                .body(request)
                .execute();
    }

    private TestHttpResponse deleteStepResponse(long todoId, List<Long> stepId) {
        String id = stepId.stream()
                .map(Object::toString)
                .collect(Collectors.joining(","));

        return testHttpClient.request()
                .path("todos/" + todoId + "/steps?stepId=" + id)
                .DELETE()
                .execute();
    }
}
