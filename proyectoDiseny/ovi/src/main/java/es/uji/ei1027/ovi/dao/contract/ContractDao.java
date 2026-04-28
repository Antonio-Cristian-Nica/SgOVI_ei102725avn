package es.uji.ei1027.ovi.dao.contract;

import es.uji.ei1027.ovi.model.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class ContractDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<Contract> getContracts() {
        return jdbcTemplate.query("SELECT * FROM CONTRACT", new ContractRowMapper());
    }

    public Contract getContract(int contractID) {
        return jdbcTemplate.queryForObject(
                "SELECT * FROM CONTRACT WHERE contractID=?",
                new ContractRowMapper(), contractID);
    }

    public void addContract(Contract contract) {
        jdbcTemplate.update(
                "INSERT INTO CONTRACT (version, startServiceDate, endServiceDate, " +
                        "status, documentURL, negotiationID) VALUES (?,?,?,?,?,?)",
                contract.getVersion(), contract.getStartServiceDate(),
                contract.getEndServiceDate(), contract.getStatus(),
                contract.getDocumentURL(), contract.getNegotiationID());
    }

    public void updateContract(Contract contract) {
        jdbcTemplate.update(
                "UPDATE CONTRACT SET version=?, startServiceDate=?, endServiceDate=?, " +
                        "status=?, documentURL=? WHERE contractID=?",
                contract.getVersion(), contract.getStartServiceDate(),
                contract.getEndServiceDate(), contract.getStatus(),
                contract.getDocumentURL(), contract.getContractID());
    }

    public void deleteContract(int contractID) {
        jdbcTemplate.update("DELETE FROM CONTRACT WHERE contractID=?", contractID);
    }

    public Contract getContractByNegotiation(int negotiationID) {
        try {
            return jdbcTemplate.queryForObject(
                    "SELECT * FROM CONTRACT WHERE negotiationID=?",
                    new ContractRowMapper(), negotiationID);
        } catch (org.springframework.dao.EmptyResultDataAccessException e) {
            return null;
        }
    }

    public List<Contract> getContractsByOviUser(int oviID) {
        return jdbcTemplate.query(
                "SELECT c.* FROM CONTRACT c " +
                        "INNER JOIN NEGOTIATION n ON c.negotiationID = n.negotiationID " +
                        "INNER JOIN ASSISTANCE_REQUEST ar ON n.requestID = ar.requestID " +
                        "WHERE ar.oviID = ?",
                new ContractRowMapper(), oviID);
    }

    public List<Contract> getContractsByPapPati(int papID) {
        return jdbcTemplate.query(
                "SELECT c.* FROM CONTRACT c " +
                        "INNER JOIN NEGOTIATION n ON c.negotiationID = n.negotiationID " +
                        "WHERE n.papID = ?",
                new ContractRowMapper(), papID);
    }
}