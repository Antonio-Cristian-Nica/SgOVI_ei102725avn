package es.uji.ei1027.ovi.dao.oviusertutor;

import es.uji.ei1027.ovi.model.Tutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class TutorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Tutor> getTutors() {
        return jdbcTemplate.query("SELECT * FROM TUTOR", new TutorRowMapper());
    }

    // Devuelve null si no existe, en lugar de lanzar excepción
    public Tutor getTutor(int tutorID) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM TUTOR WHERE tutorID=?",
                    new TutorRowMapper(), tutorID);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addTutor(Tutor tutor) {
        jdbcTemplate.update(
                "INSERT INTO TUTOR (nameAndSurname, phoneNumber, birthDate, " +
                        "homeAddress, emailAddress, relationshipWithUser) VALUES (?,?,?,?,?,?)",
                tutor.getNameAndSurname(), tutor.getPhoneNumber(),
                tutor.getBirthDate(), tutor.getHomeAddress(),
                tutor.getEmailAddress(), tutor.getRelationshipWithUser());
    }

    public void updateTutor(Tutor tutor) {
        jdbcTemplate.update(
                "UPDATE TUTOR SET nameAndSurname=?, phoneNumber=?, birthDate=?, " +
                        "homeAddress=?, emailAddress=?, relationshipWithUser=? WHERE tutorID=?",
                tutor.getNameAndSurname(), tutor.getPhoneNumber(),
                tutor.getBirthDate(), tutor.getHomeAddress(),
                tutor.getEmailAddress(), tutor.getRelationshipWithUser(),
                tutor.getTutorID());
    }

    public void deleteTutor(int tutorID) {
        jdbcTemplate.update("DELETE FROM TUTOR WHERE tutorID=?", tutorID);
    }

    public int getLastInsertedId() {
        Integer id = jdbcTemplate.queryForObject(
                "SELECT MAX(tutorID) FROM TUTOR", Integer.class);
        return id != null ? id : 0;
    }

    // Comprova si ja existeix un tutor amb aquest correu electrònic
    public boolean existsEmail(String email) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TUTOR WHERE emailAddress=?",
                Integer.class, email);
        return count != null && count > 0;
    }

    // Comprova si existeix un altre tutor amb aquest correu (excloent l'actual)
    public boolean existsEmailExcluding(String email, int currentTutorID) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM TUTOR WHERE emailAddress=? AND tutorID != ?",
                Integer.class, email, currentTutorID);
        return count != null && count > 0;
    }
}