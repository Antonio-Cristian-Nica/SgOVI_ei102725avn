package es.uji.ei1027.ovi.dao;

import es.uji.ei1027.ovi.model.Contract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public class ContractDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
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
}