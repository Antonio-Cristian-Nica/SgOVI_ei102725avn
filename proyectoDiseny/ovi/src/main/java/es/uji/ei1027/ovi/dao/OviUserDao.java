package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.OviUser;
import org.springframework.beans.factory.annotation.Autowired;
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

    public OviUser getOviUser(int oviID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM OVI_USER WHERE oviID=?",
                new OviUserRowMapper(), oviID);
    }

    public void addOviUser(OviUser oviUser) {
        jdbcTemplate.update(
                "INSERT INTO OVI_USER (nameAndSurname, phoneNumber, birthDate, " +
                        "homeAddress, emailAddress, functionalDiversity, LOPDAcceptance, " +
                        "status, tutorID) VALUES (?,?,?,?,?,?,?,?,?)",
                oviUser.getNameAndSurname(), oviUser.getPhoneNumber(),
                oviUser.getBirthDate(), oviUser.getHomeAddress(),
                oviUser.getEmailAddress(), oviUser.getFunctionalDiversity(),
                oviUser.isLOPDAcceptance(), oviUser.getStatus(),
                oviUser.getTutorID());
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
}