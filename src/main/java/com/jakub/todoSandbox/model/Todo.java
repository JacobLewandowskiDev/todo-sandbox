package com.jakub.todoSandbox.model;

import java.util.List;

public record Todo (Long id, String name, String description, Priority priority, List<Step> steps){


    public static Builder builder(String name) {
        return new Builder(name);
    }

    public static class Builder {
        private Long id;
        private String name;
        private String description;
        private Priority priority;
        private List<Step> steps;

        private Builder(String name) {
            this.name = name;
        }

        public Builder id(Long id) {
            this.id = id;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder priority(Priority priority) {
            this.priority = priority;
            return this;
        }

        public Builder steps(List<Step> steps) {
            this.steps = steps;
            return this;
        }

        public Todo build() {
            return new Todo(id, name, description, priority, steps);
        }
    }
}
