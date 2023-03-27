package com.jakub.todoSandbox.model;

import java.util.List;

public record Todo (Long id, String name, String description, Priority priority, List<Step> steps){

}
