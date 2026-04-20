package es.uji.ei1027.ovi.dao.PapPati;

import es.uji.ei1027.ovi.model.PapPati;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PapPatiRowMapper implements RowMapper<PapPati> {
    @Override
    public PapPati mapRow(ResultSet rs, int rowNum) throws SQLException {
        PapPati pap = new PapPati();
        pap.setPapID(rs.getInt("papID"));
        pap.setNameAndSurname(rs.getString("nameAndSurname"));
        pap.setPhoneNumber(rs.getString("phoneNumber"));
        pap.setBirthDate(rs.getDate("birthDate").toLocalDate());
        pap.setHomeAddress(rs.getString("homeAddress"));
        pap.setEmailAddress(rs.getString("emailAddress"));
        pap.setAcademicBackground(rs.getString("academicBackground"));
        pap.setProfessionalExperience(rs.getString("professionalExperience"));
        pap.setSpecializationAreas(rs.getString("specializationAreas"));
        pap.setDocuments(rs.getString("documents"));
        pap.setLOPDAcceptance(rs.getBoolean("LOPDAcceptance"));
        pap.setStatus(rs.getString("status"));
        pap.setUsername(rs.getString("username"));
        return pap;
    }
}