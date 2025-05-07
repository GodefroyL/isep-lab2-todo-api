package org.isep.cleancode.application;

import org.isep.cleancode.Todo;
import java.util.List;
import java.util.Optional;

public interface ITodoRepository {
    void save(Todo todo);
    List<Todo> findAll();
    Optional<Todo> findByName(String name);
}
