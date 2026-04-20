package es.uji.ei1027.ovi.dao.PapPatiSchedule;

import es.uji.ei1027.ovi.model.Schedule;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ScheduleDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Schedule> getSchedules() {
        return jdbcTemplate.query("SELECT * FROM SCHEDULE", new ScheduleRowMapper());
    }

    public List<Schedule> getSchedulesByPap(int papID) {
        return jdbcTemplate.query(
                "SELECT * FROM SCHEDULE WHERE papID=?",
                new ScheduleRowMapper(), papID);
    }

    public Schedule getSchedule(int scheduleID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM SCHEDULE WHERE scheduleID=?",
                new ScheduleRowMapper(), scheduleID);
    }

    public void addSchedule(Schedule schedule) {
        jdbcTemplate.update(
                "INSERT INTO SCHEDULE (dayOfWeek, startHour, endHour, papID) VALUES (?,?,?,?)",
                schedule.getDayOfWeek(), schedule.getStartHour(),
                schedule.getEndHour(), schedule.getPapID());
    }

    public void updateSchedule(Schedule schedule) {
        jdbcTemplate.update(
                "UPDATE SCHEDULE SET dayOfWeek=?, startHour=?, endHour=?, papID=? WHERE scheduleID=?",
                schedule.getDayOfWeek(), schedule.getStartHour(),
                schedule.getEndHour(), schedule.getPapID(), schedule.getScheduleID());
    }

    public void deleteSchedule(int scheduleID) {
        jdbcTemplate.update("DELETE FROM SCHEDULE WHERE scheduleID=?", scheduleID);
    }
}