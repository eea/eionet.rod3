package eionet.rod.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.rod.model.ClientDTO;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service to store metadata for T_CLIENT using JDBC.
 */
@Service
public class ClientServiceJdbc implements ClientService {

    //@Autowired
    private DataSource dataSource;

    public void setDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void insert(ClientDTO clientRec) {
        String query = "INSERT INTO T_CLIENT (CLIENT_NAME, CLIENT_ACRONYM, "
                + "CLIENT_ADDRESS, CLIENT_URL, POSTAL_CODE, CLIENT_EMAIL, CITY, "
                + "COUNTRY, DESCRIPTION) VALUES (?,?,?,?,?,?,?,?,?)";

        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                clientRec.getName(),
                clientRec.getAcronym(),
                clientRec.getAddress(),
                clientRec.getUrl(),
                clientRec.getPostalCode(),
                clientRec.getEmail(),
                clientRec.getCity(),
                clientRec.getCountry(),
                clientRec.getDescription()
                );
    }

    @Override
    public ClientDTO getById(Integer clientId) {
        String query = "SELECT T_CLIENT.PK_CLIENT_ID AS clientId,"
                + "CLIENT_NAME AS name, CLIENT_ACRONYM AS acronym,"
                + "CLIENT_URL AS url, CLIENT_ADDRESS AS address, CLIENT_EMAIL AS email,"
                + "DESCRIPTION, POSTAL_CODE AS postalcode, CITY, COUNTRY, CLIENT_SHORT_NAME AS shortName "
                + "FROM T_CLIENT "
                + "WHERE T_CLIENT.PK_CLIENT_ID = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        ClientDTO clientRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<ClientDTO>(ClientDTO.class), clientId);
        return clientRec;
    }

    @Override
    public List<ClientDTO> getAllClients() {
        String query = "SELECT PK_CLIENT_ID AS clientId, CLIENT_NAME AS name, CLIENT_ACRONYM AS acronym,"
                + "CLIENT_URL AS url, CLIENT_ADDRESS AS address, CLIENT_EMAIL AS email,"
                + "DESCRIPTION, POSTAL_CODE AS postalcode, CITY, COUNTRY, CLIENT_SHORT_NAME AS shortName "
                + "FROM T_CLIENT "
                + "ORDER BY name";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<ClientDTO>(ClientDTO.class));
    }

    @Override
    public void deleteById(Integer clientId) {
        String query = "DELETE FROM T_CLIENT WHERE PK_CLIENT_ID = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, clientId);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM T_CLIENT";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }

    @Override
    public void update(ClientDTO clientRec) {
        String update = "UPDATE T_CLIENT SET CLIENT_NAME=?, CLIENT_SHORT_NAME=?, "
                + "CLIENT_ACRONYM=?, CLIENT_ADDRESS=?, CLIENT_URL=?, "
                + "POSTAL_CODE=?, CLIENT_EMAIL=?, CITY=?, COUNTRY=?, DESCRIPTION=? "
                + "WHERE PK_CLIENT_ID=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(update,
                clientRec.getName(),
                clientRec.getShortName(),
                clientRec.getAcronym(),
                clientRec.getAddress(),
                clientRec.getUrl(),
                clientRec.getPostalCode(),
                clientRec.getEmail(),
                clientRec.getCity(),
                clientRec.getCountry(),
                clientRec.getDescription(),
                clientRec.getClientId());
    }

    //FIXME: Stub
    @Override
    public boolean clientExists(Integer clientId) {
        return true;
    }
}
