package es.uji.ei1027.ovi.model;

import java.time.LocalDate;

public class Contract {
    private int contractID;
    private int version;
    private LocalDate creationDate;
    private LocalDate startServiceDate;
    private LocalDate endServiceDate;
    private String status; // 'active', 'inactive', 'ended', 'cancelled'
    private String documentURL;
    private int negotiationID; // FK → Negotiation

    public int getContractID() { return contractID; }
    public void setContractID(int contractID) { this.contractID = contractID; }

    public int getVersion() { return version; }
    public void setVersion(int version) { this.version = version; }

    public LocalDate getCreationDate() { return creationDate; }
    public void setCreationDate(LocalDate creationDate) { this.creationDate = creationDate; }

    public LocalDate getStartServiceDate() { return startServiceDate; }
    public void setStartServiceDate(LocalDate startServiceDate) { this.startServiceDate = startServiceDate; }

    public LocalDate getEndServiceDate() { return endServiceDate; }
    public void setEndServiceDate(LocalDate endServiceDate) { this.endServiceDate = endServiceDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getDocumentURL() { return documentURL; }
    public void setDocumentURL(String documentURL) { this.documentURL = documentURL; }

    public int getNegotiationID() { return negotiationID; }
    public void setNegotiationID(int negotiationID) { this.negotiationID = negotiationID; }
}