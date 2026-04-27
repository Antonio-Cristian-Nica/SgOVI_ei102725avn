package es.uji.ei1027.ovi.model;

public class Instructor {
    private int instructorID;
    private String nameAndSurname;
    private String phoneNumber;
    private String emailAddress;
    private String specialization;
    private String username;

    public Instructor() {}

    public int getInstructorID() { return instructorID; }
    public void setInstructorID(int instructorID) { this.instructorID = instructorID; }

    public String getNameAndSurname() { return nameAndSurname; }
    public void setNameAndSurname(String nameAndSurname) { this.nameAndSurname = nameAndSurname; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
}
