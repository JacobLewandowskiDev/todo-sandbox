package com.jakub.todoSandbox.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Priority {
    HIGH("HIGH"),
    MEDIUM("MEDIUM"),
    LOW("LOW");

    public final String label;

    Priority(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @JsonCreator
    public static Priority getPriorityFromCode(String value) {

        for (Priority priority : Priority.values()) {
            if (priority.getLabel().equals(value)) {
                return priority;
            }
        }
        return null;
    }
}
