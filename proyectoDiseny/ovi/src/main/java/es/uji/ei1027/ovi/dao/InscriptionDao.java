package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Inscription;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class InscriptionDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Inscription> getInscriptions() {
        return jdbcTemplate.query("SELECT * FROM INSCRIPTION", new InscriptionRowMapper());
    }

    public List<Inscription> getInscriptionsByUser(int oviID) {
        return jdbcTemplate.query(
                "SELECT * FROM INSCRIPTION WHERE oviID=?",
                new InscriptionRowMapper(), oviID);
    }

    public List<Inscription> getInscriptionsByActivity(int activityID) {
        return jdbcTemplate.query(
                "SELECT * FROM INSCRIPTION WHERE activityID=?",
                new InscriptionRowMapper(), activityID);
    }

    public Inscription getInscription(int oviID, int activityID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM INSCRIPTION WHERE oviID=? AND activityID=?",
                new InscriptionRowMapper(), oviID, activityID);
    }

    public void addInscription(Inscription inscription) {
        jdbcTemplate.update(
                "INSERT INTO INSCRIPTION (oviID, activityID) VALUES (?,?)",
                inscription.getOviID(), inscription.getActivityID());
    }

    public void deleteInscription(int oviID, int activityID) {
        jdbcTemplate.update(
                "DELETE FROM INSCRIPTION WHERE oviID=? AND activityID=?",
                oviID, activityID);
    }
}