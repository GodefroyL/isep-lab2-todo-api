package org.isep.cleancode.application;

import org.isep.cleancode.Todo;

import java.util.List;

public class TodoManager {
    private final ITodoRepository repository;

    public TodoManager(ITodoRepository repository) {
        this.repository = repository;
    }

    public List<Todo> getAllTodos() {
        return repository.findAll();
    }

    public void createTodo(Todo todo) throws Exception {
        if (todo.getName() == null || todo.getName().isBlank()) {
            throw new Exception("Todo name is required.");
        }
        if (todo.getName().length() >= 64) {
            throw new Exception("Todo name must be shorter than 64 characters.");
        }
        if (repository.findByName(todo.getName()).isPresent()) {
            throw new Exception("Todo name must be unique.");
        }
        if (!todo.isValidDueDate()) {
            throw new Exception("Due date is invalid (expected yyyy-MM-dd or empty).");
        }

        repository.save(todo);
    }
}
