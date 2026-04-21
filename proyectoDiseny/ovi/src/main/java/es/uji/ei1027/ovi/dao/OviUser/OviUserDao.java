package es.uji.ei1027.ovi.dao.OviUser;

import es.uji.ei1027.ovi.model.OviUser;
import es.uji.ei1027.ovi.model.OviUserRegistration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class OviUserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<OviUser> getOviUsers() {
        return jdbcTemplate.query("SELECT * FROM OVI_USER", new OviUserRowMapper());
    }

    public List<OviUser> getOviUsersPendents() {
        return jdbcTemplate.query(
                "SELECT * FROM OVI_USER WHERE status='approvalPending'",
                new OviUserRowMapper());
    }

    public OviUser getOviUser(int oviID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM OVI_USER WHERE oviID=?",
                new OviUserRowMapper(), oviID);
    }

    public void addOviUser(OviUserRegistration oviUser) {
        jdbcTemplate.update(
                "INSERT INTO OVI_USER (nameAndSurname, phoneNumber, birthDate, " +
                        "homeAddress, emailAddress, functionalDiversity, LOPDAcceptance, " +
                        "status, tutorID, username) VALUES (?,?,?,?,?,?,?,?,?,?)",
                oviUser.getNameAndSurname(), oviUser.getPhoneNumber(),
                oviUser.getBirthDate(), oviUser.getHomeAddress(),
                oviUser.getEmailAddress(), oviUser.getFunctionalDiversity(),
                oviUser.isLOPDAcceptance(), oviUser.getStatus(),
                null, oviUser.getUsername());
    }

    public int getLastInsertedId() {
        Integer id = jdbcTemplate.queryForObject(
                "SELECT MAX(oviID) FROM OVI_USER", Integer.class);
        return id != null ? id : 0;
    }

    public void updateOviUser(OviUser oviUser) {
        jdbcTemplate.update(
                "UPDATE OVI_USER SET nameAndSurname=?, phoneNumber=?, birthDate=?, " +
                        "homeAddress=?, emailAddress=?, functionalDiversity=?, LOPDAcceptance=?, " +
                        "status=?, tutorID=? WHERE oviID=?",
                oviUser.getNameAndSurname(), oviUser.getPhoneNumber(),
                oviUser.getBirthDate(), oviUser.getHomeAddress(),
                oviUser.getEmailAddress(), oviUser.getFunctionalDiversity(),
                oviUser.isLOPDAcceptance(), oviUser.getStatus(),
                oviUser.getTutorID(), oviUser.getOviID());
    }

    public void deleteOviUser(int oviID) {
        jdbcTemplate.update("DELETE FROM OVI_USER WHERE oviID=?", oviID);
    }

    public OviUser getOviUserByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM OVI_USER WHERE username=?",
                    new OviUserRowMapper(), username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void activateOviUser(String username) {
        jdbcTemplate.update(
                "UPDATE OVI_USER SET status='active' WHERE username=?",
                username);
    }
}