package org.isep.cleancode.persistence.inmemory;

import org.isep.cleancode.Todo;
import org.isep.cleancode.application.ITodoRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class TodoInMemoryRepository implements ITodoRepository {
    private final List<Todo> todos = new ArrayList<>();

    @Override
    public void save(Todo todo) {
        todos.add(todo);
    }

    @Override
    public List<Todo> findAll() {
        return new ArrayList<>(todos);
    }

    @Override
    public Optional<Todo> findByName(String name) {
        return todos.stream()
                .filter(todo -> todo.getName().equalsIgnoreCase(name))
                .findFirst();
    }
}
