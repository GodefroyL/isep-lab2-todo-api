package org.isep.cleancode;

import com.google.gson.Gson;
import spark.Request;
import spark.Response;

import java.util.ArrayList;
import java.util.List;

public class TodoController {

    // this Todo class should be completed to achieve Step 1

    private static final Gson gson = new Gson();
    private final List<Todo> todos = new ArrayList<>();

    public Object getAllTodos(Request req, Response res) {
        res.type("application/json");

        return gson.toJson(todos);
    };

    public Object createTodo(Request req, Response res) {
        Todo newTodo = gson.fromJson(req.body(), Todo.class);

        // Rule 1: name is required
        if (newTodo.getName() == null || newTodo.getName().trim().isEmpty()) {
            res.status(400);
            return "Error: 'name' is required.";
        }

        // Rule 2: name must be < 64 characters
        if (newTodo.getName().length() >= 64) {
            res.status(400);
            return "Error: 'name' must be shorter than 64 characters.";
        }

        // Rule 3: name must be unique
        boolean exists = todos.stream()
                .anyMatch(todo -> todo.getName().equalsIgnoreCase(newTodo.getName()));
        if (exists) {
            res.status(400);
            return "Error: 'name' must be unique.";
        }

        // Rule 4: due date must be valid
        if (!newTodo.isValidDueDate()) {
            res.status(400);
            return "Error: 'dueDate' is not a valid date (expected format: yyyy-MM-dd).";
        }

        todos.add(newTodo);
        res.status(201);
        res.type("application/json");

        return gson.toJson(newTodo);
    }
}
