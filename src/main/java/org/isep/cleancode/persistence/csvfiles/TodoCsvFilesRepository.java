package org.isep.cleancode.persistence.csvfiles;

import org.isep.cleancode.Todo;
import org.isep.cleancode.application.ITodoRepository;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;
import java.util.Date;

import java.util.*;
import java.util.stream.Collectors;

public class TodoCsvFilesRepository implements ITodoRepository {

    private final Path filePath;

    public TodoCsvFilesRepository() {
        String appData = System.getenv("APPDATA");
        if (appData == null) {
            appData = System.getProperty("user.home"); // fallback
        }
        Path dir = Paths.get(appData, "todo-app");
        this.filePath = dir.resolve("todos.csv");

        try {
            Files.createDirectories(dir);
            if (!Files.exists(filePath)) {
                Files.createFile(filePath);
            }
        } catch (IOException e) {
            throw new RuntimeException("Cannot initialize CSV repository", e);
        }
    }

    @Override
    public synchronized void save(Todo todo) {
        try (BufferedWriter writer = Files.newBufferedWriter(filePath, StandardOpenOption.APPEND)) {
            writer.write(serialize(todo));
            writer.newLine();
        } catch (IOException e) {
            throw new RuntimeException("Error saving todo", e);
        }
    }

    @Override
    public synchronized List<Todo> findAll() {
        try {
            return Files.lines(filePath)
                    .filter(line -> !line.trim().isEmpty())
                    .map(this::deserialize)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new RuntimeException("Error reading todos", e);
        }
    }

    @Override
    public synchronized Optional<Todo> findByName(String name) {
        return findAll().stream()
                .filter(todo -> todo.getName().equalsIgnoreCase(name))
                .findFirst();
    }

    private String serialize(Todo todo) {
        return todo.getName().replace(",", "\\,") + ","
                + (todo.getDueDate() != null ? todo.getDueDate() : "");
    }

    private Todo deserialize(String line) {
        String[] parts = line.split("(?<!\\\\),", 2); // split on unescaped commas
        String name = parts[0].replace("\\,", ",");
        String due = parts.length > 1 ? parts[1] : null;

        Todo todo = new Todo(name, due);
        if (due != null && !due.isBlank()) {
            todo.setDueDate(due);
        }
        return todo;
    }
}
