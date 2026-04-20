package es.uji.ei1027.ovi.dao.OviUserTutor;

import es.uji.ei1027.ovi.model.Tutor;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TutorRowMapper implements RowMapper<Tutor> {
    @Override
    public Tutor mapRow(ResultSet rs, int rowNum) throws SQLException {
        Tutor tutor = new Tutor();
        tutor.setTutorID(rs.getInt("tutorID"));
        tutor.setNameAndSurname(rs.getString("nameAndSurname"));
        tutor.setPhoneNumber(rs.getString("phoneNumber"));
        tutor.setBirthDate(rs.getDate("birthDate").toLocalDate());
        tutor.setHomeAddress(rs.getString("homeAddress"));
        tutor.setEmailAddress(rs.getString("emailAddress"));
        tutor.setRelationshipWithUser(rs.getString("relationshipWithUser"));
        return tutor;
    }
}