package es.uji.ei1027.ovi.dao.requestschedule;

import es.uji.ei1027.ovi.model.RequestSchedule;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalTime;

public class RequestScheduleRowMapper implements RowMapper<RequestSchedule> {
    @Override
    public RequestSchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
        RequestSchedule schedule = new RequestSchedule();
        schedule.setReqScheduleID(rs.getInt("reqScheduleID"));
        schedule.setDate(rs.getObject("date", LocalDate.class));
        schedule.setDayOfWeek(rs.getInt("dayOfWeek"));
        schedule.setStartHour(rs.getObject("startHour", LocalTime.class));
        schedule.setEndHour(rs.getObject("endHour", LocalTime.class));
        schedule.setRequestID(rs.getInt("requestID"));
        return schedule;
    }
}