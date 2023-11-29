package com.jakub.todoSandbox;

import com.jakub.todoSandbox.model.Priority;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.support.IntegrationTest;
import com.jakub.todoSandbox.support.TestHttpResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class TodoControllerTest extends IntegrationTest {

    @Test
    void saveTodo_ValidTodoWithSteps() throws IOException {
        //Given
        List<Step> steps = new ArrayList<>();
        Step step1 = new Step(1L, "Step1", "Desc1");
        Step step2 = new Step(2L, "Step2", "Desc2");
        steps.add(step1);
        steps.add(step2);
        Todo userCreatedTodo = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);

        //When
        var saveTodoResponse = postResponse("todos", userCreatedTodo);

        //Assert
        assertResponseStatus(saveTodoResponse, HttpStatus.CREATED);
        var todoResponseBody = saveTodoResponse.objectMapper().readValue(saveTodoResponse.body(), Todo.class);

        assertNotNull(todoResponseBody);
        assertEquals(userCreatedTodo, todoResponseBody);
        assertEquals(2, todoResponseBody.steps().size());
    }

    @Test
    void saveTodo_ValidTodoWithoutSteps() throws IOException {
        //Given
        List<Step> steps = new ArrayList<>();
        var userCreatedTodo = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);

        //When
        var saveTodoResponse = postResponse("todos", userCreatedTodo);

        //Assert
        assertResponseStatus(saveTodoResponse, HttpStatus.CREATED);
        var todoResponseBody = saveTodoResponse.objectMapper().readValue(saveTodoResponse.body(), Todo.class);

        assertNotNull(todoResponseBody);
        assertEquals(userCreatedTodo, todoResponseBody);
        assertEquals(0, todoResponseBody.steps().size());
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
        long todoId = 1L;
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
        long todoId = 1L;
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
    void deleteTodo() {
        //Given
        long todoId = 1L;
        var validTodo = new Todo(todoId, "Todo", "Description", Priority.HIGH, new ArrayList<>());
        var postTodo = postResponse("todos", validTodo);

        //When
        var deleteTodoResponse = deleteResponse(todoId);

        //Assert
        assertEquals(HttpStatus.OK.value(), deleteTodoResponse.statusCode());
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

    private TestHttpResponse updateResponse(long id, Object request) {
        return testHttpClient.request()
                .path("todos/" + id)
                .PUT()
                .body(request)
                .execute();
    }

    private TestHttpResponse deleteResponse(long id) {
        return testHttpClient.request()
                .path("todos/" + id)
                .DELETE()
                .execute();
    }
}
