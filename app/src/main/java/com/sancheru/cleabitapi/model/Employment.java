package com.sancheru.cleabitapi.model;

public class Employment {
    private String role;

    private String title;

    public Employment(String role, String title) {
        this.role = role;
        this.title = title;
    }

    public String getRole() {
        return role;
    }

    public String getTitle() {
        return title;
    }
}
