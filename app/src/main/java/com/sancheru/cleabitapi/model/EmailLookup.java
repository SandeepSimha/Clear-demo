package com.sancheru.cleabitapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class EmailLookup {
    @SerializedName("person")
    @Expose
    private Person p;

    @SerializedName("company")
    @Expose
    private Company c;

    private String domain;

    public EmailLookup(Person p, Company c, String domain) {
        this.p = p;
        this.c = c;
        this.domain = domain;
    }

    public Person getP() {
        return p;
    }
}
