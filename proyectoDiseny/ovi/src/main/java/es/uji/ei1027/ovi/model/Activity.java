package es.uji.ei1027.ovi.model;

import java.time.LocalDateTime;

public class Activity {
    private int activityID;
    private String title;
    private String description;
    private LocalDateTime dateAndTime;
    private String location;
    private int maxParticipants;
    private int availableSpots;
    private int registeredUsers;
    private String status; // 'created', 'InProgress', 'ended'

    public int getActivityID() { return activityID; }
    public void setActivityID(int activityID) { this.activityID = activityID; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getDateAndTime() { return dateAndTime; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public int getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(int maxParticipants) { this.maxParticipants = maxParticipants; }

    public int getAvailableSpots() { return availableSpots; }
    public void setAvailableSpots(int availableSpots) { this.availableSpots = availableSpots; }

    public int getRegisteredUsers() { return registeredUsers; }
    public void setRegisteredUsers(int registeredUsers) { this.registeredUsers = registeredUsers; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}