package es.uji.ei1027.ovi.dao.Credentials;

import es.uji.ei1027.ovi.exceptions.SgOVIException;
import es.uji.ei1027.ovi.model.Credentials;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
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
                    new CredentialsRowMapper(), username);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addCredentials(Credentials credentials) {
        try {
            jdbcTemplate.update(
                    "INSERT INTO CREDENTIALS (username, password, role, id, activated) VALUES (?, ?, ?, ?, ?)",
                    credentials.getUsername(), credentials.getPassword(),
                    credentials.getRole(), credentials.getId(), credentials.getActivated());
        } catch (DuplicateKeyException e) {
            throw new SgOVIException("Ese nombre de usuario ya está pillado", "Usuario duplicado");
       }
    }

    public void updateId(String username, int id) {
        jdbcTemplate.update(
                "UPDATE CREDENTIALS SET id=? WHERE username=?",
                id, username);
    }

    public void updatePassword(String username, String newPassword) {
        jdbcTemplate.update(
                "UPDATE CREDENTIALS SET password=? WHERE username=?",
                newPassword, username);
    }

    public void activateCredentials(String username) {
        jdbcTemplate.update(
                "UPDATE CREDENTIALS SET activated=true WHERE username=?",
                username);
    }
}
