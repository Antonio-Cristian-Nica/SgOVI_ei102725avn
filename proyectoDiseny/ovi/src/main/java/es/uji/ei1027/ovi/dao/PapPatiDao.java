package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.PapPati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class PapPatiDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<PapPati> getPapPatis() {
        return jdbcTemplate.query("SELECT * FROM PAP_PATI", new PapPatiRowMapper());
    }

    public PapPati getPapPati(int papID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM PAP_PATI WHERE papID=?",
                new PapPatiRowMapper(), papID);
    }

    public void addPapPati(PapPati papPati) {
        jdbcTemplate.update(
                "INSERT INTO PAP_PATI (nameAndSurname, phoneNumber, birthDate, " +
                        "homeAddress, emailAddress, academicBackground, professionalExperience, " +
                        "specializationAreas, documents, LOPDAcceptance, status) VALUES (?,?,?,?,?,?,?,?,?,?,?)",
                papPati.getNameAndSurname(), papPati.getPhoneNumber(),
                papPati.getBirthDate(), papPati.getHomeAddress(),
                papPati.getEmailAddress(), papPati.getAcademicBackground(),
                papPati.getProfessionalExperience(), papPati.getSpecializationAreas(),
                papPati.getDocuments(), papPati.isLOPDAcceptance(), papPati.getStatus());
    }

    public void updatePapPati(PapPati papPati) {
        jdbcTemplate.update(
                "UPDATE PAP_PATI SET nameAndSurname=?, phoneNumber=?, birthDate=?, " +
                        "homeAddress=?, emailAddress=?, academicBackground=?, professionalExperience=?, " +
                        "specializationAreas=?, documents=?, LOPDAcceptance=?, status=? WHERE papID=?",
                papPati.getNameAndSurname(), papPati.getPhoneNumber(),
                papPati.getBirthDate(), papPati.getHomeAddress(),
                papPati.getEmailAddress(), papPati.getAcademicBackground(),
                papPati.getProfessionalExperience(), papPati.getSpecializationAreas(),
                papPati.getDocuments(), papPati.isLOPDAcceptance(),
                papPati.getStatus(), papPati.getPapID());
    }

    public void deletePapPati(int papID) {
        jdbcTemplate.update("DELETE FROM PAP_PATI WHERE papID=?", papID);
    }
}