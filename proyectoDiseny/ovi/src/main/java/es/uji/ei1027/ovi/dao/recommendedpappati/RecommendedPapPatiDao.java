package es.uji.ei1027.ovi.dao.recommendedpappati;

import es.uji.ei1027.ovi.model.RecommendedPapPati;
import org.springframework.beans.factory.annotation.Autowired;
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
                "SELECT * FROM RECOMMENDED_PAP_PATI WHERE requestID=?",
                new RecommendedPapPatiRowMapper(), requestID);
    }

    public RecommendedPapPati getRecommendedPapPati(int requestID, int papID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM RECOMMENDED_PAP_PATI WHERE requestID=? AND papID=?",
                new RecommendedPapPatiRowMapper(), requestID, papID);
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
}