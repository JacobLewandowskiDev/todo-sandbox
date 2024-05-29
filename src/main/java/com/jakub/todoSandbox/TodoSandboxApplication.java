package com.jakub.todoSandbox;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TodoSandboxApplication {
    public static void main(String[] args) {
        System.setProperty("server.servlet.context-path", "/vpt");
        SpringApplication.run(TodoSandboxApplication.class, args);
    }
}
