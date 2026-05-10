package es.uji.ei1027.ovi.dao.contract;

import es.uji.ei1027.ovi.model.Contract;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ContractRowMapper implements RowMapper<Contract> {
    @Override
    public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contract contract = new Contract();
        contract.setContractID(rs.getInt("contractID"));
        contract.setVersion(rs.getInt("version"));
        contract.setCreationDate(rs.getObject("creationDate", LocalDate.class));
        contract.setStartServiceDate(rs.getObject("startServiceDate", LocalDate.class));
        contract.setEndServiceDate(rs.getObject("endServiceDate", LocalDate.class));
        contract.setStatus(rs.getString("status"));
        contract.setDocumentURL(rs.getString("documentURL"));
        contract.setNegotiationID(rs.getInt("negotiationID"));
        return contract;
    }
}