package es.uji.ei1027.ovi.dao.negotiation;

import es.uji.ei1027.ovi.model.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class NegotiationDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Negotiation> getNegotiations() {
        return jdbcTemplate.query("SELECT * FROM NEGOTIATION ORDER BY dateAndTime DESC", new NegotiationRowMapper());
    }

    public Negotiation getNegotiation(int negotiationID) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM NEGOTIATION WHERE negotiationID=?",
                    new NegotiationRowMapper(), negotiationID);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addNegotiation(Negotiation negotiation) {
        jdbcTemplate.update(
                "INSERT INTO NEGOTIATION (duration, location, status, dateAndTime, " +
                        "conversation, oviUserConfirmed, papPatiConfirmed, requestID, papID) " +
                        "VALUES (?,?,?,?,?,?,?,?,?)",
                negotiation.getDuration(), negotiation.getLocation(),
                negotiation.getStatus(), negotiation.getDateAndTime(),
                negotiation.getConversation(), negotiation.isOviUserConfirmed(),
                negotiation.isPapPatiConfirmed(), negotiation.getRequestID(),
                negotiation.getPapID());
    }

    public void updateNegotiation(Negotiation negotiation) {
        jdbcTemplate.update(
                "UPDATE NEGOTIATION SET duration=?, location=?, status=?, dateAndTime=?, " +
                        "conversation=?, oviUserConfirmed=?, papPatiConfirmed=? WHERE negotiationID=?",
                negotiation.getDuration(), negotiation.getLocation(),
                negotiation.getStatus(), negotiation.getDateAndTime(),
                negotiation.getConversation(), negotiation.isOviUserConfirmed(),
                negotiation.isPapPatiConfirmed(), negotiation.getNegotiationID());
    }

    public void deleteNegotiation(int negotiationID) {
        jdbcTemplate.update("DELETE FROM NEGOTIATION WHERE negotiationID=?", negotiationID);
    }

    public List<Negotiation> getNegotiationsByRequest(int requestID) {
        return jdbcTemplate.query(
                "SELECT * FROM NEGOTIATION WHERE requestID=? ORDER BY dateAndTime DESC",
                new NegotiationRowMapper(), requestID);
    }

    public Negotiation getNegotiationByRequestAndPap(int requestID, int papID) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM NEGOTIATION WHERE requestID=? AND papID=?",
                    new NegotiationRowMapper(), requestID, papID);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Negotiation> getNegotiationsFinished() {
        return jdbcTemplate.query(
                "SELECT * FROM NEGOTIATION WHERE status='finished' ORDER BY dateAndTime DESC",
                new NegotiationRowMapper());
    }

    public int getLastInsertedId() {
        Integer id = jdbcTemplate.queryForObject(
                "SELECT MAX(negotiationID) FROM NEGOTIATION", Integer.class);
        return id != null ? id : 0;
    }

    public List<Negotiation> getNegotiationsByPap(int papID) {
        return jdbcTemplate.query(
                "SELECT * FROM NEGOTIATION WHERE papID=? ORDER BY dateAndTime DESC",
                new NegotiationRowMapper(), papID);
    }
}