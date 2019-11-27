package com.sancheru.cleabitapi.model;

public class Company {
    private String description;

    private Linkedin linkedin;

    private String type;

    private Twitter twitter;

    private String emailProvider;

    private String logo;

    private String id;

    private String[] tech;

    private String indexedAt;

    private Facebook facebook;

    private String domain;

    private String name;

    private String location;

    public String getDescription() {
        return description;
    }

    public Linkedin getLinkedin() {
        return linkedin;
    }

    public String getType() {
        return type;
    }

    public Twitter getTwitter() {
        return twitter;
    }

    public String getEmailProvider() {
        return emailProvider;
    }

    public String getLogo() {
        return logo;
    }

    public String getId() {
        return id;
    }

    public String[] getTech() {
        return tech;
    }

    public String getIndexedAt() {
        return indexedAt;
    }

    public Facebook getFacebook() {
        return facebook;
    }

    public String getDomain() {
        return domain;
    }

    public String getName() {
        return name;
    }

    public String getLocation() {
        return location;
    }
}
