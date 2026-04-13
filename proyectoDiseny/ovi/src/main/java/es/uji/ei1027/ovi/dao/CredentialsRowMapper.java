package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Credentials;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CredentialsRowMapper implements RowMapper<Credentials> {
    @Override
    public Credentials mapRow(ResultSet rs, int rowNum) throws SQLException {
        Credentials credentials = new Credentials();
        credentials.setUsername(rs.getString("username"));
        credentials.setPassword(rs.getString("password"));
        credentials.setRole(rs.getString("role"));
        credentials.setId(rs.getInt("id"));
        return credentials;
    }
}
