package com.sancheru.cleabitapi.model;

public class DomainModel {
    private String domain;
    private String firstName;
    private String lastName;
    private String email;
    private String position;
    private String social;
    private String znoResult;

    public DomainModel(String domain, String firstName, String lastName, String email, String position, String social, String znoResult) {
        this.domain = domain;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.position = position;
        this.social = social;
        this.znoResult = znoResult;
    }

    public String getDomain() {
        return domain;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPosition() {
        return position;
    }

    public String getSocial() {
        return social;
    }
}
