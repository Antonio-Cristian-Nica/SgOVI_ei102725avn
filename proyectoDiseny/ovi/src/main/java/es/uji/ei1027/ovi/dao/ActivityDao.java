package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Activity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ActivityDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Activity> getActivities() {
        return jdbcTemplate.query("SELECT * FROM ACTIVITY", new ActivityRowMapper());
    }

    public Activity getActivity(int activityID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM ACTIVITY WHERE activityID=?",
                new ActivityRowMapper(), activityID);
    }

    public void addActivity(Activity activity) {
        jdbcTemplate.update(
                "INSERT INTO ACTIVITY (title, description, dateAndTime, location, " +
                        "maxParticipants, availableSpots, registeredUsers, status) VALUES (?,?,?,?,?,?,?,?)",
                activity.getTitle(), activity.getDescription(),
                activity.getDateAndTime(), activity.getLocation(),
                activity.getMaxParticipants(), activity.getAvailableSpots(),
                activity.getRegisteredUsers(), activity.getStatus());
    }

    public void updateActivity(Activity activity) {
        jdbcTemplate.update(
                "UPDATE ACTIVITY SET title=?, description=?, dateAndTime=?, location=?, " +
                        "maxParticipants=?, availableSpots=?, registeredUsers=?, status=? WHERE activityID=?",
                activity.getTitle(), activity.getDescription(),
                activity.getDateAndTime(), activity.getLocation(),
                activity.getMaxParticipants(), activity.getAvailableSpots(),
                activity.getRegisteredUsers(), activity.getStatus(),
                activity.getActivityID());
    }

    public void deleteActivity(int activityID) {
        jdbcTemplate.update("DELETE FROM ACTIVITY WHERE activityID=?", activityID);
    }
}