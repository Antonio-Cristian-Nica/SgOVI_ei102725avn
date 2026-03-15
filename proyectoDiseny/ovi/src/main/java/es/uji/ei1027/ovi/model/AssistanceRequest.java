package es.uji.ei1027.ovi.model;

import java.time.LocalDateTime;

public class AssistanceRequest {
    private int requestID;
    private String serviceLocation;
    private String requiredAssistance;
    private LocalDateTime creationDate;
    private String status; // 'accepted', 'rejected', 'inProgress'
    private int oviID; // FK → OviUser

    public AssistanceRequest() {}

    public int getRequestID() { return requestID; }
    public void setRequestID(int requestID) { this.requestID = requestID; }

    public String getServiceLocation() { return serviceLocation; }
    public void setServiceLocation(String serviceLocation) { this.serviceLocation = serviceLocation; }

    public String getRequiredAssistance() { return requiredAssistance; }
    public void setRequiredAssistance(String requiredAssistance) { this.requiredAssistance = requiredAssistance; }

    public LocalDateTime getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDateTime creationDate) { this.creationDate = creationDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public int getOviID() { return oviID; }
    public void setOviID(int oviID) { this.oviID = oviID; }
}