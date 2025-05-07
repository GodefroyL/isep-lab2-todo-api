package org.isep.cleancode;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public class Todo {
    private String name;
    private String dueDate; // Format ISO, ex: "2025-05-07"

    public Todo(String name, String dueDate) {
        this.name = name;
        this.dueDate = dueDate;
    }

    public Todo() {} // Requis par Gson

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDueDate() {
        return dueDate;
    }

    public void setDueDate(String dueDate) {
        this.dueDate = dueDate;
    }

    public boolean isValidDueDate() {
        if (dueDate == null || dueDate.isEmpty()) return true;
        try {
            LocalDate.parse(dueDate);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
}
