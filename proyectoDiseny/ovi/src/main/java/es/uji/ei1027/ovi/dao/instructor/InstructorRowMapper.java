package es.uji.ei1027.ovi.dao.instructor;

import es.uji.ei1027.ovi.model.Instructor;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InstructorRowMapper implements RowMapper<Instructor> {
    @Override
    public Instructor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Instructor instructor = new Instructor();
        instructor.setInstructorID(rs.getInt("instructorID"));
        instructor.setNameAndSurname(rs.getString("nameAndSurname"));
        instructor.setPhoneNumber(rs.getString("phoneNumber"));
        instructor.setEmailAddress(rs.getString("emailAddress"));
        instructor.setSpecialization(rs.getString("specialization"));
        instructor.setUsername(rs.getString("username"));
        return instructor;
    }
}
