package com.example.inai.models;

import android.util.Log;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;

public class Event {
    private String id;
    private String creatorId;
    private String title;
    private String date;
    private String startTime;
    private String endTime;
    private String location;
    private String description;
    private String imageUrl;
    private String telegram;

    public String getCreatorId() {
        return creatorId;
    }

    private String clashString;
    private ArrayList attendees;
    private ActivityType type;

    public Event(String title, String date, String startTime, String endTime, String location,
                 String description, String telegram, ActivityType type, String imageUrl) {
    this.title = title;
    this.date = date;
    this.startTime = startTime;
    this.endTime = endTime;
    this.location = location;
    this.description = description;
    this.telegram = telegram;
    this.attendees = new ArrayList();
    this.imageUrl = imageUrl;
    this.clashString = "";
    this.type = type;
    }

    public Event(String title, String date, String startTime, String endTime, String location,
                 String description, String telegram, ActivityType type, String imageUrl, String creatorId) {
        this.title = title;
        this.date = date;
        this.startTime = startTime;
        this.endTime = endTime;
        this.location = location;
        this.description = description;
        this.telegram = telegram;
        this.attendees = new ArrayList();
        this.imageUrl = imageUrl;
        this.clashString = "";
        this.type = type;
        this.creatorId = creatorId;
    }

    public String getId() {return id;}
    public String getTitle() { return title; }
    public LocalDate getDate() {
        Log.i("EVENT", "event " + date);
        if (date == null) return null;
        return LocalDate.parse(date);
    }
    public LocalTime getStartTime() {
        Log.i("EVENT", "event " + startTime);
        if (startTime == null) return null;
        return LocalTime.parse(startTime);
    }
    public LocalTime getEndTime() {
        Log.i("EVENT", "event " + endTime);
        if (endTime == null) return null;
        return LocalTime.parse(endTime);
    }
    public String getLocation() { return location; }
    public String getDescription() { return description; }
    public String getImageUrl() { return imageUrl; }
    public String getTelegram() { return telegram; }
    public String getClashString() { return clashString; }
    public ArrayList getAttendees() { return attendees; }

    public void addAttendee(String userId) {
        this.attendees.add(userId);
    }

    public void removeAttendee(String userId) {
        this.attendees.remove(userId);
    }
    public ActivityType getType() { return type; }
}
