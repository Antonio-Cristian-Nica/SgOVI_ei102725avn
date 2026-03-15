package es.uji.ei1027.ovi.model;

import java.time.LocalDateTime;

public class Inscription {
    private int oviID;      // FK + PK
    private int activityID; // FK + PK
    private LocalDateTime dateAndTime;

    public Inscription() {}

    public int getOviID() { return oviID; }
    public void setOviID(int oviID) { this.oviID = oviID; }

    public int getActivityID() { return activityID; }
    public void setActivityID(int activityID) { this.activityID = activityID; }

    public LocalDateTime getDateAndTime() { return dateAndTime; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }
}