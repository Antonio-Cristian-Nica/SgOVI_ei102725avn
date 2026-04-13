package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import javax.sql.DataSource;

@Repository
public class CredentialsDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public Credentials getCredentials(String username) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM CREDENTIALS WHERE username=?",
                    (rs, rowNum) -> {
                        Credentials c = new Credentials();
                        c.setUsername(rs.getString("username"));
                        c.setPassword(rs.getString("password"));
                        c.setRole(rs.getString("role"));
                        c.setId(rs.getInt("id"));
                        return c;
                    }, username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addCredentials(Credentials credentials) {
        jdbcTemplate.update(
                "INSERT INTO CREDENTIALS (username, password, role, id, type) VALUES (?, ?, ?, ?, ?)",
                credentials.getUsername(), credentials.getPassword(),
                credentials.getRole(), credentials.getId());
    }
}
