package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.RequestSchedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class RequestScheduleDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<RequestSchedule> getRequestSchedules() {
        return jdbcTemplate.query("SELECT * FROM REQUEST_SCHEDULE", new RequestScheduleRowMapper());
    }

    public List<RequestSchedule> getRequestSchedulesByRequest(int requestID) {
        return jdbcTemplate.query(
                "SELECT * FROM REQUEST_SCHEDULE WHERE requestID=?",
                new RequestScheduleRowMapper(), requestID);
    }

    public RequestSchedule getRequestSchedule(int reqScheduleID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM REQUEST_SCHEDULE WHERE reqScheduleID=?",
                new RequestScheduleRowMapper(), reqScheduleID);
    }

    public void addRequestSchedule(RequestSchedule requestSchedule) {
        jdbcTemplate.update(
                "INSERT INTO REQUEST_SCHEDULE (date, dayOfWeek, startHour, endHour, requestID) " +
                        "VALUES (?,?,?,?,?)",
                requestSchedule.getDate(), requestSchedule.getDayOfWeek(),
                requestSchedule.getStartHour(), requestSchedule.getEndHour(),
                requestSchedule.getRequestID());
    }

    public void updateRequestSchedule(RequestSchedule requestSchedule) {
        jdbcTemplate.update(
                "UPDATE REQUEST_SCHEDULE SET date=?, dayOfWeek=?, startHour=?, endHour=? " +
                        "WHERE reqScheduleID=?",
                requestSchedule.getDate(), requestSchedule.getDayOfWeek(),
                requestSchedule.getStartHour(), requestSchedule.getEndHour(),
                requestSchedule.getReqScheduleID());
    }

    public void deleteRequestSchedule(int reqScheduleID) {
        jdbcTemplate.update("DELETE FROM REQUEST_SCHEDULE WHERE reqScheduleID=?", reqScheduleID);
    }
}