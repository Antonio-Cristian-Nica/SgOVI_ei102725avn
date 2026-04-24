package es.uji.ei1027.ovi.model;

import java.time.LocalDateTime;

public class RecommendedPapPati {
    private int requestID; // FK + PK
    private int papID;     // FK + PK
    private LocalDateTime dateOfRecommendation;

    public int getRequestID() { return requestID; }
    public void setRequestID(int requestID) { this.requestID = requestID; }

    public int getPapID() { return papID; }
    public void setPapID(int papID) { this.papID = papID; }

    public LocalDateTime getDateOfRecommendation() { return dateOfRecommendation; }
    public void setDateOfRecommendation(LocalDateTime dateOfRecommendation) { this.dateOfRecommendation = dateOfRecommendation; }
}