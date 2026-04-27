package es.uji.ei1027.ovi.dao.instructor;

import es.uji.ei1027.ovi.model.Instructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class InstructorDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Instructor> getInstructors() {
        return jdbcTemplate.query(
                "SELECT * FROM INSTRUCTOR",
                new InstructorRowMapper());
    }

    public Instructor getInstructor(int instructorID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM INSTRUCTOR WHERE instructorID=?",
                new InstructorRowMapper(), instructorID);
    }

    public Instructor getInstructorByUsername(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM INSTRUCTOR WHERE username=?",
                    new InstructorRowMapper(), username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addInstructor(Instructor instructor) {
        jdbcTemplate.update(
                "INSERT INTO INSTRUCTOR (nameAndSurname, phoneNumber, emailAddress, " +
                        "specialization, username) VALUES (?,?,?,?,?)",
                instructor.getNameAndSurname(), instructor.getPhoneNumber(),
                instructor.getEmailAddress(), instructor.getSpecialization(),
                instructor.getUsername());
    }

    public int getLastInsertedId() {
        Integer id = jdbcTemplate.queryForObject(
                "SELECT MAX(instructorID) FROM INSTRUCTOR", Integer.class);
        return id != null ? id : 0;
    }

    public void updateInstructor(Instructor instructor) {
        jdbcTemplate.update(
                "UPDATE INSTRUCTOR SET nameAndSurname=?, phoneNumber=?, emailAddress=?, " +
                        "specialization=? WHERE instructorID=?",
                instructor.getNameAndSurname(), instructor.getPhoneNumber(),
                instructor.getEmailAddress(), instructor.getSpecialization(),
                instructor.getInstructorID());
    }

    public void deleteInstructor(int instructorID) {
        jdbcTemplate.update(
                "DELETE FROM INSTRUCTOR WHERE instructorID=?",
                instructorID);
    }
}
