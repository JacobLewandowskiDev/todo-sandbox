package com.jakub.todoSandbox.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class Todo {
    private Long id;
    private String name;
    private String description;
    private Priority priority;
    private List<Step> nestedSteps;


    public Todo(Long id, String name, String description, Priority priority, List<Step> nestedSteps) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.priority = priority;
        this.nestedSteps = nestedSteps;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Priority getPriority() {
        return priority;
    }

    public void setPriority(Priority priority) {
        this.priority = priority;
    }

    public List<Step> getNestedSteps() {
        return nestedSteps;
    }

    public void setNestedSteps(List<Step> nestedSteps) {
        this.nestedSteps = nestedSteps;
    }

}
