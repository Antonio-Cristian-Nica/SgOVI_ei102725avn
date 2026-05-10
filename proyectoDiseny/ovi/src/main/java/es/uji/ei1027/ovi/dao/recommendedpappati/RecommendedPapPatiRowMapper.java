package es.uji.ei1027.ovi.dao.recommendedpappati;

import es.uji.ei1027.ovi.model.RecommendedPapPati;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

public class RecommendedPapPatiRowMapper implements RowMapper<RecommendedPapPati> {
    @Override
    public RecommendedPapPati mapRow(ResultSet rs, int rowNum) throws SQLException {
        RecommendedPapPati rec = new RecommendedPapPati();
        rec.setRequestID(rs.getInt("requestID"));
        rec.setPapID(rs.getInt("papID"));
        rec.setDateOfRecommendation(rs.getObject("dateOfRecommendation", LocalDateTime.class));
        return rec;
    }
}