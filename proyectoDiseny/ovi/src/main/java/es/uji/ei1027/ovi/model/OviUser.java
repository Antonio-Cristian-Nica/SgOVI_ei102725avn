package es.uji.ei1027.ovi.model;

import java.time.LocalDate;

public class OviUser {
    private int oviID;
    private String nameAndSurname;
    private String phoneNumber;
    private LocalDate birthDate;
    private String homeAddress;
    private String emailAddress;
    private String functionalDiversity;
    private boolean LOPDAcceptance;
    private String status; // 'active', 'inactive', 'approvalPending'
    private Integer tutorID; // Nullable (FK)
    private String username;

    public String getUsername() { return username;}
    public void setUsername(String username) {this.username = username;}

    public int getOviID() { return oviID; }
    public void setOviID(int oviID) { this.oviID = oviID; }

    public String getNameAndSurname() { return nameAndSurname; }
    public void setNameAndSurname(String nameAndSurname) { this.nameAndSurname = nameAndSurname; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public LocalDate getBirthDate() { return birthDate; }
    public void setBirthDate(LocalDate birthDate) { this.birthDate = birthDate; }

    public String getHomeAddress() { return homeAddress; }
    public void setHomeAddress(String homeAddress) { this.homeAddress = homeAddress; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getFunctionalDiversity() { return functionalDiversity; }
    public void setFunctionalDiversity(String functionalDiversity) { this.functionalDiversity = functionalDiversity; }

    public boolean isLOPDAcceptance() { return LOPDAcceptance; }
    public void setLOPDAcceptance(boolean LOPDAcceptance) { this.LOPDAcceptance = LOPDAcceptance; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Integer getTutorID() { return tutorID; }
    public void setTutorID(Integer tutorID) { this.tutorID = tutorID; }
}