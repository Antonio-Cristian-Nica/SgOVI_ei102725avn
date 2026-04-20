package es.uji.ei1027.ovi.dao.Activity;

import es.uji.ei1027.ovi.model.Activity;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ActivityRowMapper implements RowMapper<Activity> {
    @Override
    public Activity mapRow(ResultSet rs, int rowNum) throws SQLException {
        Activity activity = new Activity();
        activity.setActivityID(rs.getInt("activityID"));
        activity.setTitle(rs.getString("title"));
        activity.setDescription(rs.getString("description"));
        activity.setDateAndTime(rs.getTimestamp("dateAndTime").toLocalDateTime());
        activity.setLocation(rs.getString("location"));
        activity.setMaxParticipants(rs.getInt("maxParticipants"));
        activity.setAvailableSpots(rs.getInt("availableSpots"));
        activity.setRegisteredUsers(rs.getInt("registeredUsers"));
        activity.setStatus(rs.getString("status"));
        return activity;
    }
}