package es.uji.ei1027.ovi.dao.oviuser;

import es.uji.ei1027.ovi.model.OviUser;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OviUserRowMapper implements RowMapper<OviUser> {
    @Override
    public OviUser mapRow(ResultSet rs, int rowNum) throws SQLException {
        OviUser user = new OviUser();
        user.setOviID(rs.getInt("oviID"));
        user.setNameAndSurname(rs.getString("nameAndSurname"));
        user.setPhoneNumber(rs.getString("phoneNumber"));
        user.setBirthDate(rs.getDate("birthDate").toLocalDate());
        user.setHomeAddress(rs.getString("homeAddress"));
        user.setEmailAddress(rs.getString("emailAddress"));
        user.setFunctionalDiversity(rs.getString("functionalDiversity"));
        user.setLOPDAcceptance(rs.getBoolean("LOPDAcceptance"));
        user.setStatus(rs.getString("status"));
        user.setUsername(rs.getString("username"));

        // tutorID es nullable
        int tutorID = rs.getInt("tutorID");
        user.setTutorID(rs.wasNull() ? null : tutorID);

        return user;
    }
}