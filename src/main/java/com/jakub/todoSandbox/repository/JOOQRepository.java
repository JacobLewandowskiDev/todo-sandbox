package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.model.Priority;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import org.jooq.DSLContext;
import org.jooq.Record;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import static com.jakub.todoSandbox.jooq.tables.Todo.TODO;
import static com.jakub.todoSandbox.jooq.tables.Step.STEP;

import com.jakub.todoSandbox.jooq.enums.PriorityEnum;
import com.jakub.todoSandbox.jooq.tables.records.TodoRecord;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class JOOQRepository implements TodoRepository {

    private final DSLContext context;

    public JOOQRepository(DSLContext context) {
        this.context = context;
    }

    @Override
    public Optional<Todo> findTodoById(long todoId) {
        var record = context.select()
                .from(TODO)
                .where(TODO.ID.eq(todoId))
                .fetchOne();

        return Optional.ofNullable(record)
                .map(r -> {
                    TodoRecord todoRecord = r.into(TODO);
                    List<Step> steps = context.select(STEP.ID, STEP.NAME, STEP.DESCRIPTION)
                            .from(STEP)
                            .where(STEP.TODO_ID.eq(todoId))
                            .fetch()
                            .map(stepRecord -> new Step(
                                    stepRecord.get(STEP.ID),
                                    stepRecord.get(STEP.NAME),
                                    stepRecord.get(STEP.DESCRIPTION)
                            ));

                    return Todo.builder(todoRecord.getName())
                            .id(todoRecord.getId())
                            .description(todoRecord.getDescription())
                            .priority(Priority.valueOf(todoRecord.getPriority().name()))
                            .steps(steps)
                            .build();
                });
    }

    @Override
    public List<Todo> findAllTodos() {
        List<Record> result = context.select()
                .from(TODO)
                .orderBy(TODO.PRIORITY.desc())
                .fetch();

        return result.stream()
                .map(record -> Todo.builder(record.get(TODO.NAME))
                        .id(record.get(TODO.ID))
                        .priority(Priority.valueOf(record.get(TODO.PRIORITY).name()))
                        .build())
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public long saveTodo(Todo todo) {
        TodoRecord todoRecord = context.insertInto(TODO)
                .set(TODO.NAME, todo.name())
                .set(TODO.DESCRIPTION, todo.description())
                .set(TODO.PRIORITY, PriorityEnum.valueOf(todo.priority().name()))
                .returning()
                .fetchOne();

        todo.steps().forEach(step -> context.insertInto(STEP)
                .set(STEP.NAME, step.name())
                .set(STEP.DESCRIPTION, step.description())
                .set(STEP.TODO_ID, todoRecord.getId())
                .execute());

        List<Step> savedSteps = context.selectFrom(STEP)
                .where(STEP.TODO_ID.eq(todoRecord.getId()))
                .fetch()
                .map(stepRecord -> new Step(
                        stepRecord.getId(),
                        stepRecord.getName(),
                        stepRecord.getDescription()))
                .stream().toList();

        return todoRecord.getId();
    }

    @Transactional
    @Override
    public void updateTodo(long todoId, Todo todo) {
        var updatedRecord = context.update(TODO)
                .set(TODO.NAME, todo.name())
                .set(TODO.DESCRIPTION, todo.description())
                .set(TODO.PRIORITY, PriorityEnum.valueOf(todo.priority().name()))
                .where(TODO.ID.eq(todoId))
                .execute();

        if (updatedRecord > 0) {
            System.out.println("Todo using todoId: {" + todoId + "} has been updated.");
        } else {
            throw new ValidationException("Update operation failed for todo with todoId: {" + todoId + "}");
        }
    }

    @Override
    public Optional<Todo> deleteTodo(long todoId) {
        Optional<Todo> deletedTodo = findTodoById(todoId);
        if (deletedTodo.isPresent()) {
            context.delete(TODO)
                    .where(TODO.ID.eq(todoId))
                    .execute();
        }
        return deletedTodo;
    }

    @Transactional
    @Override
    public void saveSteps(long todoId, List<Step> createdSteps) {
        for (Step step : createdSteps) {
            var addStep = context.insertInto(STEP)
                    .set(STEP.NAME, step.name())
                    .set(STEP.DESCRIPTION, step.description())
                    .set(STEP.TODO_ID, todoId)
                    .execute();
        }
    }

    @Transactional
    @Override
    public void updateStep(long todoId, Step updatedStep) {
        var updatedRows = context.update(STEP)
                .set(STEP.NAME, updatedStep.name())
                .set(STEP.DESCRIPTION, updatedStep.description())
                .where(STEP.ID.eq(updatedStep.id()))
                .and(STEP.TODO_ID.eq(todoId))
                .execute();
        if (updatedRows > 0) {
            System.out.println("Step with stepId: {" + updatedStep.id() + "} has been updated");
        } else {
            throw new ValidationException("Failed to update the step with stepId: {" + updatedStep.id() + "}");
        }
    }

    @Override
    public void deleteSteps(long todoId, List<Long> stepIds) {
        context.deleteFrom(STEP)
                .where(STEP.ID.in(stepIds))
                .and(STEP.TODO_ID.eq(todoId))
                .execute();
    }
}
