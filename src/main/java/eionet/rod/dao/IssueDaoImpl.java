package eionet.rod.dao;

import eionet.rod.model.Issue;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.util.List;

/**
 * @author ycarrasco
 */
@Repository
@Transactional
public class IssueDaoImpl implements IssueDao {

    private static final Log logger = LogFactory.getLog(IssueDaoImpl.class);

    public IssueDaoImpl() {
    }

    private JdbcTemplate jdbcTemplate;

    @Resource
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public List<Issue> findObligationIssuesList(Integer issueOblId) {
        String query = "SELECT T_ISSUE.ISSUE_NAME as issueName, T_ISSUE.PK_ISSUE_ID as issueId "
                + "FROM T_ISSUE, T_RAISSUE_LNK "
                + "WHERE T_RAISSUE_LNK.FK_ISSUE_ID=T_ISSUE.PK_ISSUE_ID AND T_RAISSUE_LNK.FK_RA_ID= ? "
                + "ORDER BY T_ISSUE.ISSUE_NAME";

        try {
            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Issue.class), issueOblId);
        } catch (DataAccessException e) {
            logger.debug(e, e);
            throw new ResourceNotFoundException("The obligation you requested with id " + issueOblId + " was not found in the database", e);
        }
    }

    @Override
    public List<Issue> findAllIssuesList() {
        String query = "SELECT T_ISSUE.ISSUE_NAME as issueName, T_ISSUE.PK_ISSUE_ID as issueId "
                + "FROM T_ISSUE "
                + "ORDER BY T_ISSUE.ISSUE_NAME";

        try {

            return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Issue.class));

        } catch (DataAccessException e) {
            logger.debug(e, e);
            throw new ResourceNotFoundException("The Issues not found in the database", e);
        }
    }


    @Override
    public Issue findById(Integer issueId) {
        String query = "SELECT T_ISSUE.ISSUE_NAME as issueName, T_ISSUE.PK_ISSUE_ID as issueId "
                + "FROM T_ISSUE "
                + "Where PK_ISSUE_ID = ?";

        try {

            return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Issue.class), issueId);

        } catch (DataAccessException e) {
            logger.debug(e, e);
            throw new ResourceNotFoundException("The Issue with id " + issueId + "  not found in the database", e);
        }
    }

}
