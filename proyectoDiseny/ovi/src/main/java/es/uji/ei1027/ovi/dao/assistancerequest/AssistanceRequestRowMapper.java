package es.uji.ei1027.ovi.dao.assistancerequest;

import es.uji.ei1027.ovi.model.AssistanceRequest;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AssistanceRequestRowMapper implements RowMapper<AssistanceRequest> {
    @Override
    public AssistanceRequest mapRow(ResultSet rs, int rowNum) throws SQLException {
        AssistanceRequest request = new AssistanceRequest();
        request.setRequestID(rs.getInt("requestID"));
        request.setServiceLocation(rs.getString("serviceLocation"));
        request.setRequiredAssistance(rs.getString("requiredAssistance"));
        request.setCreationDate(rs.getObject("creationDate", LocalDateTime.class));
        request.setStatus(rs.getString("status"));
        request.setOviID(rs.getInt("oviID"));
        request.setType(rs.getString("type"));
        request.setStartServiceDate(rs.getObject("startServiceDate", LocalDate.class));
        request.setEndServiceDate(rs.getObject("endServiceDate", LocalDate.class));
        return request;
    }
}