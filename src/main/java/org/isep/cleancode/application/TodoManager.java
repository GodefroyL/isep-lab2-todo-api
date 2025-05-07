package org.isep.cleancode.application;

import org.isep.cleancode.Todo;
import org.isep.cleancode.persistence.TodoRepository;

import java.util.List;

public class TodoManager {
    private final TodoRepository repository;

    public TodoManager(TodoRepository repository) {
        this.repository = repository;
    }

    public List<Todo> getAllTodos() {
        return repository.findAll();
    }

    public void createTodo(Todo todo) throws Exception {
        // Rule: name required
        if (todo.getName() == null || todo.getName().trim().isEmpty()) {
            throw new Exception("Error: 'name' is required.");
        }

        // Rule: name < 64 characters
        if (todo.getName().length() >= 64) {
            throw new Exception("Error: 'name' must be shorter than 64 characters.");
        }

        // Rule: name unique
        if (repository.findByName(todo.getName()).isPresent()) {
            throw new Exception("Error: 'name' must be unique.");
        }

        // Rule: valid due date
        if (!todo.isValidDueDate()) {
            throw new Exception("Error: 'dueDate' is not a valid date (yyyy-MM-dd).");
        }

        repository.save(todo);
    }
}
