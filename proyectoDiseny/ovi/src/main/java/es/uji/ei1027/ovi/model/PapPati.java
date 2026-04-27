package es.uji.ei1027.ovi.model;

import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

public class PapPati {
    private int papID;
    private String nameAndSurname;
    private String phoneNumber;
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    private LocalDate birthDate;
    private String homeAddress;
    private String locality;
    private String emailAddress;
    private String academicBackground;
    private String professionalExperience;
    private String specializationAreas;
    private String documents;
    private boolean LOPDAcceptance;
    private String status; // 'active', 'inactive', 'approvalPending'
    private String username;

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public int getPapID() { return papID; }
    public void setPapID(int papID) { this.papID = papID; }

    public String getNameAndSurname() { return nameAndSurname; }
    public void setNameAndSurname(String nameAndSurname) { this.nameAndSurname = nameAndSurname; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public String getLocality() { return locality; }
    public void setLocality(String locality) { this.locality = locality; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getAcademicBackground() { return academicBackground; }
    public void setAcademicBackground(String academicBackground) { this.academicBackground = academicBackground; }

    public String getProfessionalExperience() { return professionalExperience; }
    public void setProfessionalExperience(String professionalExperience) { this.professionalExperience = professionalExperience; }

    public String getSpecializationAreas() { return specializationAreas; }
    public void setSpecializationAreas(String specializationAreas) { this.specializationAreas = specializationAreas; }

    public String getDocuments() { return documents; }
    public void setDocuments(String documents) { this.documents = documents; }

    public boolean isLOPDAcceptance() { return LOPDAcceptance; }
    public void setLOPDAcceptance(boolean LOPDAcceptance) { this.LOPDAcceptance = LOPDAcceptance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}