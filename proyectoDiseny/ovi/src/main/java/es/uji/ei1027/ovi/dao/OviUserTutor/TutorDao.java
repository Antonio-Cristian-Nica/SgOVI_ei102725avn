package es.uji.ei1027.ovi.dao.OviUserTutor;

import es.uji.ei1027.ovi.model.Tutor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class TutorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Tutor> getTutors() {
        return jdbcTemplate.query("SELECT * FROM TUTOR", new TutorRowMapper());
    }

    public Tutor getTutor(int tutorID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM TUTOR WHERE tutorID=?",
                new TutorRowMapper(), tutorID);
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
}