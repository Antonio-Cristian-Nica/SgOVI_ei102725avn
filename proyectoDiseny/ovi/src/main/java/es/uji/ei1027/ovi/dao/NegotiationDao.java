package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Negotiation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class NegotiationDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<Negotiation> getNegotiations() {
        return jdbcTemplate.query("SELECT * FROM NEGOTIATION", new NegotiationRowMapper());
    }

    public Negotiation getNegotiation(int negotiationID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM NEGOTIATION WHERE negotiationID=?",
                new NegotiationRowMapper(), negotiationID);
    }

    public void addNegotiation(Negotiation negotiation) {
        jdbcTemplate.update(
                "INSERT INTO NEGOTIATION (duration, location, status, dateAndTime, " +
                        "conversation, requestID, papID) VALUES (?,?,?,?,?,?,?)",
                negotiation.getDuration(), negotiation.getLocation(),
                negotiation.getStatus(), negotiation.getDateAndTime(),
                negotiation.getConversation(), negotiation.getRequestID(),
                negotiation.getPapID());
    }

    public void updateNegotiation(Negotiation negotiation) {
        jdbcTemplate.update(
                "UPDATE NEGOTIATION SET duration=?, location=?, status=?, dateAndTime=?, " +
                        "conversation=? WHERE negotiationID=?",
                negotiation.getDuration(), negotiation.getLocation(),
                negotiation.getStatus(), negotiation.getDateAndTime(),
                negotiation.getConversation(), negotiation.getNegotiationID());
    }

    public void deleteNegotiation(int negotiationID) {
        jdbcTemplate.update("DELETE FROM NEGOTIATION WHERE negotiationID=?", negotiationID);
    }
}