package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Inscription;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class InscriptionRowMapper implements RowMapper<Inscription> {
    @Override
    public Inscription mapRow(ResultSet rs, int rowNum) throws SQLException {
        Inscription inscription = new Inscription();
        inscription.setOviID(rs.getInt("oviID"));
        inscription.setActivityID(rs.getInt("activityID"));
        inscription.setDateAndTime(rs.getTimestamp("dateAndTime").toLocalDateTime());
        return inscription;
    }
}