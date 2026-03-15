package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Schedule;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ScheduleRowMapper implements RowMapper<Schedule> {
    @Override
    public Schedule mapRow(ResultSet rs, int rowNum) throws SQLException {
        Schedule schedule = new Schedule();
        schedule.setScheduleID(rs.getInt("scheduleID"));
        schedule.setDayOfWeek(rs.getInt("dayOfWeek"));
        schedule.setStartHour(rs.getTime("startHour").toLocalTime());
        schedule.setEndHour(rs.getTime("endHour").toLocalTime());
        schedule.setPapID(rs.getInt("papID"));
        return schedule;
    }
}