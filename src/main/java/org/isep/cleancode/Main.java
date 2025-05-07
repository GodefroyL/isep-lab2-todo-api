package org.isep.cleancode;

import org.isep.cleancode.application.ITodoRepository;
import org.isep.cleancode.application.TodoManager;
import org.isep.cleancode.persistence.TodoRepository;
import org.isep.cleancode.presentation.TodoController;

import static spark.Spark.*;

public class Main {
    public static void main(String[] args) {
        port(4567);

        ITodoRepository repo = new TodoRepository(); // adapter
        TodoManager manager = new TodoManager(repo); // port
        TodoController controller = new TodoController(manager);

        get("/todos", controller::getAllTodos);
        post("/todos", controller::createTodo);
    }
}
