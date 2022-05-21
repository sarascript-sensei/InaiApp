package com.example.inai.models;

import java.util.ArrayList;

public class User {
    private String name;
    private String email;
    private String studentId;
    private String id;
    private int permission; // 0 normal, 1 creator
    private ArrayList<String> events;
    private ArrayList<String> organisedEvents;


    public User(String name, String email, String studentId) {
        this.name = name;
        this.email = email;
        this.studentId = studentId;
        this.events = new ArrayList();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public void setEvents(ArrayList events) {
        this.events = events;
    }

    public void setPermission(int permission) {
        this.permission = permission;
    }

    public int getPermission() {
        return permission;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getStudentId() {
        return studentId;
    }

    public ArrayList<String> getEvents() { return events; }

    public void signUp(String eventId) {
        this.events.add(eventId);
    }

    public void cancelAttendance(String eventId) {
        this.events.remove(eventId);
    }

    public ArrayList<String> getOrganisedEvents() {
        return organisedEvents;
    }

    public void setOrganisedEvents(ArrayList<String> organisedEvents) {
        if (this.permission == 1) this.organisedEvents = organisedEvents;
    }
}