package es.uji.ei1027.ovi.dao.pappati;

import es.uji.ei1027.ovi.model.PapPati;
import es.uji.ei1027.ovi.model.PapPatiRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class PapPatiDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<PapPati> getPapPatis() {
        return jdbcTemplate.query("SELECT * FROM PAP_PATI", new PapPatiRowMapper());
    }

    public void addPapPati(PapPatiRegistration papPati) {
        jdbcTemplate.update(
                "INSERT INTO PAP_PATI (nameAndSurname, phoneNumber, birthDate, " +
                        "homeAddress, locality, emailAddress, academicBackground, professionalExperience, " +
                        "specializationAreas, documents, LOPDAcceptance, status, username) " +
                        "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)",
                papPati.getNameAndSurname(), papPati.getPhoneNumber(),
                papPati.getBirthDate(), papPati.getHomeAddress(),
                papPati.getLocality(), papPati.getEmailAddress(),
                papPati.getAcademicBackground(), papPati.getProfessionalExperience(),
                papPati.getSpecializationAreas(), papPati.getDocuments(),
                papPati.isLOPDAcceptance(), papPati.getStatus(),
                papPati.getUsername());
    }

    public int getLastInsertedId() {
        Integer id = jdbcTemplate.queryForObject(
                "SELECT MAX(papID) FROM PAP_PATI", Integer.class);
        return id != null ? id : 0;
    }

    public void updatePapPati(PapPati papPati) {
        jdbcTemplate.update(
                "UPDATE PAP_PATI SET nameAndSurname=?, phoneNumber=?, birthDate=?, " +
                        "homeAddress=?, locality=?, emailAddress=?, academicBackground=?, " +
                        "professionalExperience=?, specializationAreas=?, documents=?, " +
                        "LOPDAcceptance=?, status=? WHERE papID=?",
                papPati.getNameAndSurname(), papPati.getPhoneNumber(),
                papPati.getBirthDate(), papPati.getHomeAddress(),
                papPati.getLocality(), papPati.getEmailAddress(),
                papPati.getAcademicBackground(), papPati.getProfessionalExperience(),
                papPati.getSpecializationAreas(), papPati.getDocuments(),
                papPati.isLOPDAcceptance(), papPati.getStatus(),
                papPati.getPapID());
    }

    public PapPati getPapPatiByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM PAP_PATI WHERE username=?",
                    new PapPatiRowMapper(), username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<PapPati> getPapPatisPendents() {
        return jdbcTemplate.query(
                "SELECT * FROM PAP_PATI WHERE status='approvalPending'",
                new PapPatiRowMapper());
    }

    public void activatePapPati(String username) {
        jdbcTemplate.update(
                "UPDATE PAP_PATI SET status='active' WHERE username=?",
                username);
    }

    public void rejectPapPati(String username) {
        jdbcTemplate.update(
                "UPDATE PAP_PATI SET status='inactive' WHERE username=?",
                username);
    }

    public List<PapPati> getPapPatisGestionats() {
        return jdbcTemplate.query(
                "SELECT * FROM PAP_PATI WHERE status='active' OR status='inactive'",
                new PapPatiRowMapper());
    }

    public int countByStatus(String status) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM PAP_PATI WHERE status=?",
                Integer.class, status);
        return count != null ? count : 0;
    }

    public List<PapPati> getPapPatisByIDs(List<Integer> ids) {
        String placeholders = ids.stream()
                .map(id -> "?")
                .collect(java.util.stream.Collectors.joining(", "));
        return jdbcTemplate.query(
                "SELECT * FROM PAP_PATI WHERE papID IN (" + placeholders + ") AND status='active'",
                new PapPatiRowMapper(),
                ids.toArray());
    }

    public PapPati getPapPati(int papID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM PAP_PATI WHERE papID=?",
                new PapPatiRowMapper(), papID);
    }
}