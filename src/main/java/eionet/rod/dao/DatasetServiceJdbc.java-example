package eionet.datalake.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.sql.DataSource;

import eionet.datalake.model.Dataset;
import eionet.datalake.model.TestResult;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Service to store metadata for datasets using JDBC.
 */
@Service
public class DatasetServiceJdbc implements DatasetService {

    @Autowired
    private DataSource dataSource;

    @Override
    public void save(Dataset datasetRec) {
        String query = "INSERT INTO datasets (datasetid, title, latestedition) VALUES (?, ?, ?)";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query,
                datasetRec.getDatasetId(),
                datasetRec.getTitle(),
                datasetRec.getLatestEdition()
                );
    }

    @Override
    public Dataset getById(String datasetId) {
        String query = "SELECT datasets.datasetid, title, keep, keepFailures, latestEdition, rdfconfiguration,"
                + " counttests, countfailures, filename FROM datasets"
                + " JOIN editions ON editionid=latestedition"
                + " WHERE datasets.datasetid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

        Dataset datasetRec = jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<Dataset>(Dataset.class), datasetId);
        return datasetRec;
    }

    /**
     * Get all editions, and only the attributes that are relevant.
     */
    @Override
    public List<Dataset> getAll() {
        String query = "SELECT datasets.datasetid, title, latestedition, uploadtime, counttests, countfailures, filename FROM datasets"
                + " JOIN editions ON editionid=latestedition";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        return jdbcTemplate.query(query, new BeanPropertyRowMapper<Dataset>(Dataset.class));
    }

    @Override
    public void deleteById(String datasetId) {
        String query = "DELETE FROM datasets WHERE datasetid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query, datasetId);
    }

    @Override
    public void deleteAll() {
        String query = "DELETE FROM datasets";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(query);
    }

    @Override
    public void updateToLatest(String datasetId) {
        String update = "UPDATE datasets SET latestedition = (SELECT editionId FROM editions WHERE datasetid=? ORDER BY uploadtime DESC LIMIT 1) WHERE datasetid=?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(update, datasetId, datasetId);
    }

    @Override
    public void update(Dataset datasetRec) {
        String update = "UPDATE datasets SET title = ?, keep = ?, keepFailures = ?, rdfConfiguration = ? WHERE datasetid = ?";
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        jdbcTemplate.update(update,
                datasetRec.getTitle(),
                datasetRec.getKeep(),
                datasetRec.getKeepFailures(),
                datasetRec.getRdfConfiguration(),
                datasetRec.getDatasetId());
    }

}
