package com.sancheru.cleabitapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Results {
    @SerializedName("id")
    @Expose
    private String id;

    @SerializedName("role")
    @Expose
    private String role;

    @SerializedName("name")
    @Expose
    private Name name;

    @SerializedName("title")
    @Expose
    private String title;

    @SerializedName("email")
    @Expose
    private String email;

    @SerializedName("verified")
    @Expose
    private boolean verified;

    public String getId() {
        return id;
    }

    public String getRole() {
        return role;
    }

    public Name getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public String getEmail() {
        return email;
    }

    public boolean isVerified() {
        return verified;
    }
}