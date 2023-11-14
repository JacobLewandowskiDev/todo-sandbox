package com.jakub.todoSandbox;

import com.jakub.todoSandbox.model.Priority;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.support.IntegrationTest;
import com.jakub.todoSandbox.support.TestHttpResponse;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class TodoControllerTest extends IntegrationTest {

    @Test
    void saveTodo_ValidTodoWithoutSteps() throws IOException {
        //Given
        List<Step> steps = new ArrayList<>();
        var createValidTodo = new Todo(1L, "Todo1", "Description1", Priority.LOW, steps);

        //When
        var createTodoResponse = postResponse("todos", createValidTodo);

        //Assert
        Assertions.assertEquals(createTodoResponse.statusCode(), 201);
        var saveTodoResponseBody = createTodoResponse.objectMapper().readValue(createTodoResponse.body(), Todo.class);

        assertNotNull(saveTodoResponseBody);
        assertEquals("Todo1", saveTodoResponseBody.name());
        assertEquals("Description1", saveTodoResponseBody.description());
        assertEquals(Priority.LOW, saveTodoResponseBody.priority());
        assertEquals(0, saveTodoResponseBody.steps().size());
    }

    private TestHttpResponse postResponse(String urlPath, Object body) {
        return testHttpClient.request()
                .path(urlPath)
                .POST()
                .body(body)
                .execute();
    }
}
