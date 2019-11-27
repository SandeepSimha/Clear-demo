package com.sancheru.cleabitapi.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Person {

    @SerializedName("facebook")
    @Expose
    private Facebook facebook;

    @SerializedName("linkedin")
    @Expose
    private Linkedin linkedin;

    @SerializedName("twitter")
    @Expose
    private Twitter twitter;

    @SerializedName("employment")
    @Expose
    private Employment employment;

    @SerializedName("name")
    @Expose
    private Name name;

    @SerializedName("email")
    @Expose
    private String email;

    private String noResult;

    public String getNoResult() {
        return noResult;
    }

    public void setNoResult(String noResult) {
        this.noResult = noResult;
    }

    public Facebook getFacebook() {
        return facebook;
    }

    public Linkedin getLinkedin() {
        return linkedin;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public Name getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Employment getEmployment() {
        return employment;
    }
}
