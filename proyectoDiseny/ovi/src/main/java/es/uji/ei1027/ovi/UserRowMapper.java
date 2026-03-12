package es.uji.ei1027.ovi;

import es.uji.ei1027.ovi.User;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public final class UserRowMapper implements RowMapper<User> {
    public User mapRow(ResultSet rs, int rowNum) throws SQLException {
        User user = new User();
        user.setNameAndSurname(rs.getString("nameAndSurname"));
        user.setPhoneNumber(rs.getInt("phoneNumber"));
        user.setBirthdate(rs.getObject("birthdate", LocalDate.class));
        user.setHomeAddress(rs.getString("homeAddress"));
        user.setFunctionalDiversity(rs.getString("functionalDiversity"));
        user.setOVILearning(rs.getString("OVILearning"));
        user.setLOPDAcceptance(rs.getBoolean("LOPDAcceptance"));
        user.setStatus(rs.getString("status"));
        return user;
    }
}
