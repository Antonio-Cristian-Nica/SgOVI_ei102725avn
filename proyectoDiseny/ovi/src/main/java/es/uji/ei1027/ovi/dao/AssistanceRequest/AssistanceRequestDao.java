package es.uji.ei1027.ovi.dao.AssistanceRequest;

import es.uji.ei1027.ovi.model.AssistanceRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class AssistanceRequestDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<AssistanceRequest> getAssistanceRequests() {
        return jdbcTemplate.query("SELECT * FROM ASSISTANCE_REQUEST", new AssistanceRequestRowMapper());
    }

    public List<AssistanceRequest> getAssistanceRequestsByUser(int oviID) {
        return jdbcTemplate.query(
                "SELECT * FROM ASSISTANCE_REQUEST WHERE oviID=?",
                new AssistanceRequestRowMapper(), oviID);
    }

    public AssistanceRequest getAssistanceRequest(int requestID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM ASSISTANCE_REQUEST WHERE requestID=?",
                new AssistanceRequestRowMapper(), requestID);
    }

    public void addAssistanceRequest(AssistanceRequest request) {
        jdbcTemplate.update(
                "INSERT INTO ASSISTANCE_REQUEST (serviceLocation, requiredAssistance, status, oviID) " +
                        "VALUES (?,?,?,?)",
                request.getServiceLocation(), request.getRequiredAssistance(),
                request.getStatus(), request.getOviID());
    }

    public void updateAssistanceRequest(AssistanceRequest request) {
        jdbcTemplate.update(
                "UPDATE ASSISTANCE_REQUEST SET serviceLocation=?, requiredAssistance=?, " +
                        "status=? WHERE requestID=?",
                request.getServiceLocation(), request.getRequiredAssistance(),
                request.getStatus(), request.getRequestID());
    }

    public void deleteAssistanceRequest(int requestID) {
        jdbcTemplate.update("DELETE FROM ASSISTANCE_REQUEST WHERE requestID=?", requestID);
    }
}