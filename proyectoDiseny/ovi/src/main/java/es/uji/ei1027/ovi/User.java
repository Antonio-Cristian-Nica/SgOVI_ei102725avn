package es.uji.ei1027.ovi;

import java.time.LocalDate;

public class User {
    private String OVIID;
    private String nameAndSurname;
    private int phoneNumber;
    private LocalDate birthdate;
    private String homeAddress;
    private String functionalDiversity;
    private String OVILearning;
    private boolean LOPDAcceptance;
    private String status;

    public String getOVIID() {
        return OVIID;
    }

    public void setOVIID(String OVIID) {
        this.OVIID = OVIID;
    }

    public String getNameAndSurname() {
        return nameAndSurname;
    }

    public void setNameAndSurname(String nameAndSurname) {
        this.nameAndSurname = nameAndSurname;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalDate getBirthdate() {
        return birthdate;
    }

    public void setBirthdate(LocalDate birthdate) {
        this.birthdate = birthdate;
    }

    public String getHomeAddress() {
        return homeAddress;
    }

    public void setHomeAddress(String homeAddress) {
        this.homeAddress = homeAddress;
    }

    public String getFunctionalDiversity() {
        return functionalDiversity;
    }

    public void setFunctionalDiversity(String functionalDiversity) {
        this.functionalDiversity = functionalDiversity;
    }

    public String getOVILearning() {
        return OVILearning;
    }

    public void setOVILearning(String OVILearning) {
        this.OVILearning = OVILearning;
    }

    public boolean isLOPDAcceptance() {
        return LOPDAcceptance;
    }

    public void setLOPDAcceptance(boolean LOPDAcceptance) {
        this.LOPDAcceptance = LOPDAcceptance;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "User{" +
                "OVIID='" + OVIID + '\'' +
                ", nameAndSurname='" + nameAndSurname + '\'' +
                ", phoneNumber=" + phoneNumber +
                ", birthdate=" + birthdate +
                ", homeAddress='" + homeAddress + '\'' +
                ", functionalDiversity='" + functionalDiversity + '\'' +
                ", OVILearning='" + OVILearning + '\'' +
                ", LOPDAcceptance=" + LOPDAcceptance +
                ", status='" + status + '\'' +
                '}';
    }
}
