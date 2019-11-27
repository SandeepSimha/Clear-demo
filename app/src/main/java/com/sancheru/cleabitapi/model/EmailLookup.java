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


    public Person getP() {
        return p;
    }
}
