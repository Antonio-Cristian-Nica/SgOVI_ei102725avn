package es.uji.ei1027.ovi.dao.recommendedpappati;

import es.uji.ei1027.ovi.model.RecommendedPapPati;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class RecommendedPapPatiDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<RecommendedPapPati> getRecommendedPapPatis() {
        return jdbcTemplate.query("SELECT * FROM RECOMMENDED_PAP_PATI", new RecommendedPapPatiRowMapper());
    }

    public List<RecommendedPapPati> getRecommendedByRequest(int requestID) {
        return jdbcTemplate.query(
                "SELECT * FROM RECOMMENDED_PAP_PATI WHERE requestID=? ORDER BY dateOfRecommendation DESC",
                new RecommendedPapPatiRowMapper(), requestID);
    }

    public RecommendedPapPati getRecommendedPapPati(int requestID, int papID) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM RECOMMENDED_PAP_PATI WHERE requestID=? AND papID=?",
                    new RecommendedPapPatiRowMapper(), requestID, papID);
        } catch (EmptyResultDataAccessException e) {
            return null;
        }
    }

    public void addRecommendedPapPati(RecommendedPapPati recommended) {
        jdbcTemplate.update(
                "INSERT INTO RECOMMENDED_PAP_PATI (requestID, papID) VALUES (?,?)",
                recommended.getRequestID(), recommended.getPapID());
    }

    public void deleteRecommendedPapPati(int requestID, int papID) {
        jdbcTemplate.update(
                "DELETE FROM RECOMMENDED_PAP_PATI WHERE requestID=? AND papID=?",
                requestID, papID);
    }

    public boolean hasNegotiation(int requestID, int papID) {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM NEGOTIATION WHERE requestID=? AND papID=?",
                Integer.class, requestID, papID);
        return count != null && count > 0;
    }

    public List<RecommendedPapPati> getRecommendedByPap(int papID) {
        return jdbcTemplate.query(
                "SELECT * FROM RECOMMENDED_PAP_PATI WHERE papID=? ORDER BY dateOfRecommendation DESC",
                new RecommendedPapPatiRowMapper(), papID);
    }
}