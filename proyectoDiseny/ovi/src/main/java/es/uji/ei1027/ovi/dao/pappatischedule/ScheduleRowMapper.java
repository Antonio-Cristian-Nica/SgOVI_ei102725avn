package es.uji.ei1027.ovi.dao.pappatischedule;

import es.uji.ei1027.ovi.model.Schedule;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalTime;

public class ScheduleRowMapper implements RowMapper<Schedule> {
    @Override
    public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleID(rs.getInt("scheduleID"));
        schedule.setDayOfWeek(rs.getInt("dayOfWeek"));
        schedule.setStartHour(rs.getObject("startHour", LocalTime.class));
        schedule.setEndHour(rs.getObject("endHour", LocalTime.class));
        schedule.setPapID(rs.getInt("papID"));
        return schedule;
    }
}