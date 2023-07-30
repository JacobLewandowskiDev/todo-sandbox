package com.jakub.todoSandbox.repository;

import com.jakub.todoSandbox.jooq.sample.model.enums.PriorityEnum;
import com.jakub.todoSandbox.jooq.sample.model.tables.records.TodoRecord;
import com.jakub.todoSandbox.model.Priority;
import com.jakub.todoSandbox.model.Step;
import com.jakub.todoSandbox.model.Todo;
import com.jakub.todoSandbox.model.ValidationException;
import com.jakub.todoSandbox.service.TodoService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.jakub.todoSandbox.jooq.sample.model.tables.Todo.TODO;
import static com.jakub.todoSandbox.jooq.sample.model.tables.Step.STEP;

import java.util.*;

@Repository
public class JOOQRepository implements TodoRepository {

    @Autowired
    private final DSLContext context;

    private final TodoService todoService;

    public JOOQRepository(TodoService todoService, DSLContext context) {
        this.todoService = todoService;
        this.context = context;
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public Optional<Todo> findTodoById(long todoId) {
        System.out.println("findTodoById() method call with todoId: {" + todoId + "}.");
        return context.selectFrom(TODO)
                .where(TODO.ID.eq(todoId))
                .fetchOptional()
                .map(record -> {
                    List<Step> steps = context.selectFrom(STEP)
                            .where(STEP.TODO_ID.eq(todoId))
                            .fetchInto(Step.class);

                    return Todo.builder(record.getName())
                            .id(record.getId())
                            .description(record.getDescription())
                            .priority(Priority.valueOf(record.getPriority().name()))
                            .steps(steps)
                            .build();
                });
    }

    @Transactional(propagation = Propagation.SUPPORTS, readOnly = true)
    @Override
    public List<Todo> findAllTodos() {
        System.out.println("findAllTodos() method call");
        return todoService.sortByPriority(context.selectFrom(TODO)
                .fetch()
                .map(record -> Todo.builder(record.getName())
                        .id(record.getId())
                        .description(record.getDescription())
                        .priority(Priority.valueOf(record.getPriority().name()))
                        .steps(fetchSteps(record.getId()))
                        .build()));
    }

    @Transactional
    @Override
    public Todo saveTodo(Todo todo) throws ValidationException {
        System.out.println("saveTodo() method call");
        if (todoService.validateNameAndDesc(todo.name(), todo.description())) {
            TodoRecord todoRecord = context.insertInto(TODO)
                    .set(TODO.NAME, todo.name())
                    .set(TODO.DESCRIPTION, todo.description())
                    .set(TODO.PRIORITY, PriorityEnum.valueOf(todo.priority().name()))
                    .returning()
                    .fetchOne();

            for (Step step : todo.steps()) {
                assert todoRecord != null;
                context.insertInto(STEP)
                        .set(STEP.NAME, step.name())
                        .set(STEP.DESCRIPTION, step.description())
                        .set(STEP.TODO_ID, todoRecord.getId())
                        .execute();
            }

            assert todoRecord != null;
            List<Step> savedSteps = context.selectFrom(STEP)
                    .where(STEP.TODO_ID.eq(todoRecord.getId()))
                    .fetchInto(Step.class);

            return Todo.builder(todoRecord.getName())
                    .description(todoRecord.getDescription())
                    .priority(Priority.valueOf(todoRecord.getPriority().name()))
                    .steps(savedSteps)
                    .build();
        } else throw new ValidationException("The name, or description for the todo " + todo.name()
                + "with todoId: : {" + todo.id() + "} did not pass the validation");
    }

    @Transactional
    @Override
    public void updateTodo(Long todoId, Todo todo) throws ValidationException {
        System.out.println("updateTodo() method call");
        if (context.selectFrom(TODO).where(TODO.ID.eq(todoId)).fetchOne() != null) {
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
        } else {
            throw new ValidationException("No todo exists under todoId: {" + todoId + "}");
        }
    }

    @Transactional
    @Override
    public Optional<Todo> deleteTodo(Long todoId) {
        Optional<Todo> deletedTodo = findTodoById(todoId);
        if (deletedTodo.isPresent()) {
            context.delete(TODO)
                    .where(TODO.ID.eq(todoId))
                    .execute();
        }
        System.out.println("deleteTodo() method call with todoId: " + todoId + ".");
        return deletedTodo;
    }

    @Transactional
    @Override
    public void saveSteps(Long todoId, List<Step> createdSteps) throws ValidationException {
        System.out.println("saveSteps() method called with todoId: {" + todoId + "].");
        Todo doesTodoExist = findTodoById(todoId)
                .orElseThrow(() -> new ValidationException("Todo with id " + todoId + " not found"));

        if (doesTodoExist != null) {
            for (Step step : createdSteps) {
                if (todoService.canAddStepToTodo(doesTodoExist) && todoService.validateNameAndDesc(step.name(), step.description())) {
                    var addStep = context.insertInto(STEP)
                            .set(STEP.NAME, step.name())
                            .set(STEP.DESCRIPTION, step.description())
                            .set(STEP.TODO_ID, doesTodoExist.id())
                            .execute();
                } else {
                    throw new ValidationException("You have reached the maximum number of steps of 10 for todo with id: " + doesTodoExist.id()
                            + ", or a step has not passed the name/description validation process.");
                }
            }
        } else {
            throw new ValidationException("Creation of step was not possible, due to either non existing todo, " +
                    "or the new steps have not passed the validation process.");
        }
    }

    @Transactional
    @Override
    public void updateStep(Long todoId, Step updatedStep) throws ValidationException {
        System.out.println("updateStep() method called with todoId: " + todoId);

        Todo todoToUpdate = findTodoById(todoId)
                .orElseThrow(() -> new ValidationException("No Todo has been found for todoId: " + todoId));

        boolean stepExists = todoToUpdate.steps().stream()
                .anyMatch(step -> Objects.equals(step.id(), updatedStep.id()));

        if (stepExists) {
            if (todoService.validateNameAndDesc(updatedStep.name(), updatedStep.description())) {
                var updatedRows = context.update(STEP)
                        .set(STEP.NAME, updatedStep.name())
                        .set(STEP.DESCRIPTION, updatedStep.description())
                        .where(STEP.ID.eq(updatedStep.id()))
                        .and(STEP.TODO_ID.eq(todoId))
                        .execute();

                if (updatedRows > 0) {
                    System.out.println("Step with stepId: " + updatedStep.id() + " has been updated");
                } else {
                    throw new ValidationException("Failed to update the step with stepId: " + updatedStep.id());
                }
            } else {
                throw new ValidationException("The updated step did not pass the validation process.");
            }
        } else {
            throw new ValidationException("The step to update was not found in the todo with todoId: " + todoId);
        }
    }

    @Transactional
    @Override
    public void deleteSteps(Long todoId, List<Long> stepIds) throws ValidationException {
        System.out.println("deleteSteps() method called with todoId: {" + todoId + "}.");

        Todo doesTodoExist = findTodoById(todoId)
                .orElseThrow(() -> new ValidationException("No Todo has been found for todoId: {" + todoId + "}."));

        if (doesTodoExist != null) {
            for (Long stepId : stepIds) {
                boolean stepExists = doesTodoExist.steps().stream()
                        .anyMatch(step -> Objects.equals(step.id(), stepId));

                if (stepExists) {
                    var deletedRows = context.deleteFrom(STEP)
                            .where(STEP.ID.eq(stepId))
                            .and(STEP.TODO_ID.eq(todoId))
                            .execute();

                    if (deletedRows > 0) {
                        System.out.println("Step with stepId: {" + stepId + "} was removed.");
                    } else {
                        throw new ValidationException("Failed to delete the step with stepId: {" + stepId + "}.");
                    }
                } else {
                    throw new ValidationException("The step with stepId: {" + stepId + "} does not exist in the todo with todoId: {" + todoId + "}.");
                }
            }
        } else {
            throw new ValidationException("Todo with the provided id does not exist.");
        }
    }

    private List<Step> fetchSteps(Long todoId) {
        return context.selectFrom(STEP)
                .where(STEP.TODO_ID.eq(todoId))
                .fetch()
                .map(stepRecord ->
                        new Step(
                                stepRecord.getId(),
                                stepRecord.getName(),
                                stepRecord.getDescription(),
                                stepRecord.getTodoId()
                        ));
    }
}
