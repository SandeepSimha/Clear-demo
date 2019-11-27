package com.sancheru.cleabitapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.Arrays;

public class Contact {

    @SerializedName("results")
    @Expose
    private Results[] results;


    public ArrayList getResults() {
        return new ArrayList(Arrays.asList(results));
    }

    public Results[] getResultsArray() {
        return results;
    }
}
