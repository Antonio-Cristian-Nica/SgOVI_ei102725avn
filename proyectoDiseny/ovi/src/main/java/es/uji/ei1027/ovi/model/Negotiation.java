package es.uji.ei1027.ovi.model;

import java.time.LocalDateTime;

public class Negotiation {
    private int negotiationID;
    private String duration;
    private String location;
    private String status; // 'inCourse', 'inProgress', 'finished'
    private LocalDateTime dateAndTime;
    private String conversation;
    private int requestID; // FK compuesta → RecommendedPapPati
    private int papID;     // FK compuesta → RecommendedPapPati

    public Negotiation() {}

    public int getNegotiationID() { return negotiationID; }
    public void setNegotiationID(int negotiationID) { this.negotiationID = negotiationID; }

    public String getDuration() { return duration; }
    public void setDuration(String duration) { this.duration = duration; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getDateAndTime() { return dateAndTime; }
    public void setDateAndTime(LocalDateTime dateAndTime) { this.dateAndTime = dateAndTime; }

    public String getConversation() { return conversation; }
    public void setConversation(String conversation) { this.conversation = conversation; }

    public int getRequestID() { return requestID; }
    public void setRequestID(int requestID) { this.requestID = requestID; }

    public int getPapID() { return papID; }
    public void setPapID(int papID) { this.papID = papID; }
}