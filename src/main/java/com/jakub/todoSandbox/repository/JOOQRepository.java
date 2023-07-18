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


    private final Map<Long, Todo> todos = new HashMap<Long, Todo>();
    private final TodoService todoService;

    public JOOQRepository(TodoService todoService, DSLContext context) {
        this.todoService = todoService;
        this.context = context;
    }

    @Override
    public Optional<Todo> findTodoById(Long todoId) {
        System.out.println("findTodoById() method call with Todo Id: {" + todoId + "}.");
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
                context.insertInto(STEP)
                        .set(STEP.NAME, step.name())
                        .set(STEP.DESCRIPTION, step.description())
                        .set(STEP.TODO_ID, todoRecord.getId())
                        .execute();
            }

            List<Step> savedSteps = context.selectFrom(STEP)
                    .where(STEP.TODO_ID.eq(todoRecord.getId()))
                    .fetchInto(Step.class);

            return Todo.builder(todoRecord.getName())
                    .description(todoRecord.getDescription())
                    .priority(Priority.valueOf(todoRecord.getPriority().name()))
                    .steps(savedSteps)
                    .build();
        } else throw new ValidationException("The name, or description for the todo " + todo.name()
                + "with id: : {" + todo.id() + "} did not pass the validation");
    }

    @Override
    public void updateTodo(Long todoId, Todo todo) throws ValidationException {
        System.out.println("updateTodo() method call");
        if (context.selectFrom(TODO).where(TODO.ID.eq(todoId)).fetchOne() != null) {
            int updatedRecord = context.update(TODO)
                    .set(TODO.NAME, todo.name())
                    .set(TODO.DESCRIPTION, todo.description())
                    .set(TODO.PRIORITY, PriorityEnum.valueOf(todo.priority().name()))
                    .where(TODO.ID.eq(todoId))
                    .execute();

            if (updatedRecord > 0) {
                System.out.println("Todo using id: {" + todoId + "} has been updated.");
            } else {
                throw new ValidationException("Update operation failed for todo with id: {" + todoId + "}");
            }
        } else {
            throw new ValidationException("No todo exists under id: {" + todoId + "}");
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
        System.out.println("deleteTodo() method call with Todo id: " + todoId + ".");
        return deletedTodo;
    }

    @Override
    public void saveSteps(Long todoId, List<Step> createdSteps) throws ValidationException {
        var existingTodo = todos.get(todoId);
        if (existingTodo != null) {
            System.out.println("Size of step array before adding new: " + existingTodo.steps().size());
            for (Step step : createdSteps) {
                if (todoService.canAddStepToTodo(existingTodo) && todoService.validateNameAndDesc(step.name(), step.description())) {
//                    Step createdStep = todoService.createNewStepForTodo(existingTodo, step);
//                    existingTodo.steps().add(createdStep);
                } else {
                    throw new ValidationException("You have reached the maximum number of steps of 10 for todo with id: " + existingTodo.id()
                            + ", or a step has not passed the name/description validation process.");
                }
            }
            System.out.println("Size of step array after adding new: " + existingTodo.steps().size());
        } else {
            throw new ValidationException("Creation of step was not possible, due to either non existing todo, " +
                    "or the new steps have not passed the validation process.");
        }
    }

    @Override
    public void updateStep(Long todoId, Step updatedStep) throws ValidationException {
        var existingTodo = todos.get(todoId);
        if (existingTodo != null) {
            List<Step> steps = existingTodo.steps();
            Optional<Step> stepToUpdate = steps.stream()
                    .filter(step -> Objects.equals(step.id(), updatedStep.id()))
                    .findFirst();
            if (stepToUpdate.isPresent()) {
                int index = steps.indexOf(stepToUpdate.get());
                if (todoService.validateNameAndDesc(updatedStep.name(), updatedStep.description())) {
                    steps.set(index, updatedStep);
                    System.out.println("Step with id:" + updatedStep.id() + " has been updated");
                } else {
                    throw new ValidationException("The updated step did not pass the validation process.");
                }
            }
        } else {
            throw new ValidationException("Update was not possible, due to either the step not existing or the Id was invalid");
        }
    }

    @Override
    public void deleteSteps(Long todoId, List<Long> stepIds) {
        var existingTodo = todos.get(todoId);
        if (existingTodo != null) {
            System.out.println("Size of step array before removing step: " + existingTodo.steps().size());
            Iterator<Step> iterator = existingTodo.steps().iterator();
            while (iterator.hasNext()) {
                Step step = iterator.next();
                if (stepIds.contains(step.id())) {
                    iterator.remove();
                    System.out.println("Step with id:" + step.id() + " was removed.");
                }
            }
            System.out.println("Size of step array after removing step: " + existingTodo.steps().size());
        } else {
            System.out.println("No such todo with this id exists.");
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


    //    @Override
//    public Optional<Todo> findTodoById(Long todoId) {
//        return Optional.ofNullable(todos.get(todoId));
//    }

//    @Override
    //    public List<Todo> findAllTodos() {
//        List<Todo> todoMapToList = new ArrayList<>(todos.values());
//        return todoService.sortByPriority(todoMapToList);
//    }

//    @Override
//    public Todo deleteTodo(Long todoId) {
//        return todos.remove(todoId);
//    }

    //    @Override
//    public Todo saveTodo(Todo todo) throws ValidationException {
//        if (todoService.validateNameAndDesc(todo.name(), todo.description())) {
//            var toBeSavedTodo = new Todo((maxTodoId + 1L), todo.name(), todo.description(), todo.priority(), todo.steps());
//            todos.put(toBeSavedTodo.id(), toBeSavedTodo);
//            maxTodoId = toBeSavedTodo.id();
//            System.out.println(
//                    "New todo was created:" +
//                            "\n id: " + toBeSavedTodo.id()
//                            + " \n name: " + toBeSavedTodo.name()
//                            + "\n description: " + toBeSavedTodo.description()
//                            + "\n priority: " + toBeSavedTodo.priority()
//                            + "\n steps: \n" + toBeSavedTodo.steps().get(0).name());
//            return toBeSavedTodo;
//        } else {
//            throw new ValidationException("The name, or description for the todo " + todo.name()
//                    + "with id: :" + todo.id() + " did not pass the validation");
//        }
//    }

//    @Override
//    public void updateTodo(Long todoId, Todo todo) throws ValidationException {
//        var existingTodo = todos.get(todoId);
//        if (existingTodo != null && todoId != null) {
//            todos.put(todoId, todoService.createNewTodoId(todoId, todo));
//            System.out.println("Todo using id: {" + todoId + "} has been updated.");
//        } else {
//            throw new ValidationException("No todo exists under id: " + todoId);
//        }
//    }
}
