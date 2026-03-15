package es.uji.ei1027.ovi.model;

import java.time.LocalDate;

public class Tutor {
    private int tutorID;
    private String nameAndSurname;
    private String phoneNumber;
    private LocalDate birthDate;
    private String homeAddress;
    private String emailAddress;
    private String relationshipWithUser;

    public Tutor() {}

    public int getTutorID() { return tutorID; }
    public void setTutorID(int tutorID) { this.tutorID = tutorID; }

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

    public String getRelationshipWithUser() { return relationshipWithUser; }
    public void setRelationshipWithUser(String relationshipWithUser) { this.relationshipWithUser = relationshipWithUser; }
}