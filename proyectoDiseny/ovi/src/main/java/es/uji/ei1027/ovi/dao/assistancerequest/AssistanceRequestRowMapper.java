package es.uji.ei1027.ovi.dao.assistancerequest;

import es.uji.ei1027.ovi.model.AssistanceRequest;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AssistanceRequestRowMapper implements RowMapper<AssistanceRequest> {
    @Override
    public AssistanceRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        AssistanceRequest request = new AssistanceRequest();
        request.setRequestID(rs.getInt("requestID"));
        request.setServiceLocation(rs.getString("serviceLocation"));
        request.setRequiredAssistance(rs.getString("requiredAssistance"));
        request.setCreationDate(rs.getTimestamp("creationDate").toLocalDateTime());
        request.setStatus(rs.getString("status"));
        request.setOviID(rs.getInt("oviID"));
        return request;
    }
}