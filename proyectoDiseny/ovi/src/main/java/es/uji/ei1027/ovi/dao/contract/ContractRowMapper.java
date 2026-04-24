package es.uji.ei1027.ovi.dao.contract;

import es.uji.ei1027.ovi.model.Contract;
import org.springframework.jdbc.core.RowMapper;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ContractRowMapper implements RowMapper<Contract> {
    @Override
    public Contract mapRow(ResultSet rs, int rowNum) throws SQLException {
        Contract contract = new Contract();
        contract.setContractID(rs.getInt("contractID"));
        contract.setVersion(rs.getInt("version"));
        contract.setCreationDate(rs.getDate("creationDate").toLocalDate());
        contract.setStartServiceDate(rs.getDate("startServiceDate").toLocalDate());
        contract.setEndServiceDate(rs.getDate("endServiceDate").toLocalDate());
        contract.setStatus(rs.getString("status"));
        contract.setDocumentURL(rs.getString("documentURL"));
        contract.setNegotiationID(rs.getInt("negotiationID"));
        return contract;
    }
}