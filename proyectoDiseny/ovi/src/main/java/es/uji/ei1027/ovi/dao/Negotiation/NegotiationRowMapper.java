package es.uji.ei1027.ovi.dao.Negotiation;

import es.uji.ei1027.ovi.model.Negotiation;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class NegotiationRowMapper implements RowMapper<Negotiation> {
    @Override
    public Negotiation mapRow(ResultSet rs, int rowNum) throws SQLException {
        Negotiation negotiation = new Negotiation();
        negotiation.setNegotiationID(rs.getInt("negotiationID"));
        negotiation.setDuration(rs.getString("duration"));
        negotiation.setLocation(rs.getString("location"));
        negotiation.setStatus(rs.getString("status"));
        negotiation.setDateAndTime(rs.getTimestamp("dateAndTime").toLocalDateTime());
        negotiation.setConversation(rs.getString("conversation"));
        negotiation.setRequestID(rs.getInt("requestID"));
        negotiation.setPapID(rs.getInt("papID"));
        return negotiation;
    }
}