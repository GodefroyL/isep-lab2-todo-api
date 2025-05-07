package org.isep.cleancode.persistence;

import org.isep.cleancode.Todo;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoRepository {
    private final List<Todo> todos = new ArrayList<>();

    public List<Todo> findAll() {
        return todos;
    }

    public Optional<Todo> findByName(String name) {
        return todos.stream()
                .filter(todo -> todo.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    public void save(Todo todo) {
        todos.add(todo);
    }
}
