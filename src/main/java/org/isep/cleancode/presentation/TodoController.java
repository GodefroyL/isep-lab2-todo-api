package org.isep.cleancode.presentation;

import com.google.gson.Gson;
import org.isep.cleancode.Todo;
import org.isep.cleancode.application.TodoManager;

import spark.Request;
import spark.Response;

public class TodoController {
    private final Gson gson = new Gson();
    private final TodoManager todoManager;

    public TodoController(TodoManager todoManager) {
        this.todoManager = todoManager;
    }

    public Object getAllTodos(Request req, Response res) {
        res.type("application/json");
        return gson.toJson(todoManager.getAllTodos());
    }

    public Object createTodo(Request req, Response res) {
        Todo newTodo = gson.fromJson(req.body(), Todo.class);
        try {
            todoManager.createTodo(newTodo);
            res.status(201);
            return gson.toJson(newTodo);
        } catch (Exception e) {
            res.status(400);
            return e.getMessage();
        }
    }
}
