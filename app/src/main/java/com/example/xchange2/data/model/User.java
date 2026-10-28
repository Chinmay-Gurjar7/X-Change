package com.example.xchange2.data.model;

public class User {
    private String id;
    private String name;
    private String email;
    private String location;
    private String avatarUrl;

    public User() {}

    public User(String id, String name, String email, String location, String avatarUrl) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.location = location;
        this.avatarUrl = avatarUrl;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getAvatarUrl() { return avatarUrl; }
    public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }
}