package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.RequestSchedule;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class RequestScheduleRowMapper implements RowMapper<RequestSchedule> {
    @Override
    public RequestSchedule mapRow(ResultSet rs, int rowNum) throws SQLException {
        RequestSchedule rs2 = new RequestSchedule();
        rs2.setReqScheduleID(rs.getInt("reqScheduleID"));
        rs2.setDate(rs.getDate("date").toLocalDate());
        rs2.setDayOfWeek(rs.getInt("dayOfWeek"));
        rs2.setStartHour(rs.getTime("startHour").toLocalTime());
        rs2.setEndHour(rs.getTime("endHour").toLocalTime());
        rs2.setRequestID(rs.getInt("requestID"));
        return rs2;
    }
}