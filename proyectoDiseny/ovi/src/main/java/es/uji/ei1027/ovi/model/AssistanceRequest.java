package es.uji.ei1027.ovi.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssistanceRequest {
    private int requestID;
    private String serviceLocation;
    private String requiredAssistance;
    private LocalDateTime creationDate;
    private String type;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate startServiceDate;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate endServiceDate;
    private String status; // 'inProgress', 'accepted', 'rejected', 'closedWithContract', 'closedContractEnded'
    private int oviID; // FK → OviUser

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

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public LocalDate getStartServiceDate() { return startServiceDate; }
    public void setStartServiceDate(LocalDate startServiceDate) { this.startServiceDate = startServiceDate; }

    public LocalDate getEndServiceDate() { return endServiceDate; }
    public void setEndServiceDate(LocalDate endServiceDate) { this.endServiceDate = endServiceDate; }
}