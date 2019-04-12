package eionet.rod.dao;

import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.Issue;
import eionet.rod.util.exception.ResourceNotFoundException;
/**
 * 
 * @author ycarrasco
 *
 */
@Repository
@Transactional
public class IssueDaoImpl implements IssueDao{

	
	public IssueDaoImpl() {
	}
	
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Override
	public List<Issue> findObligationIssuesList(Integer IssueOblId){
		String query = "SELECT T_ISSUE.ISSUE_NAME as issueName, T_ISSUE.PK_ISSUE_ID as issueId " 
				+ "FROM T_ISSUE, T_RAISSUE_LNK "
				+ "WHERE T_RAISSUE_LNK.FK_ISSUE_ID=T_ISSUE.PK_ISSUE_ID AND T_RAISSUE_LNK.FK_RA_ID= ? " 
				+ "ORDER BY T_ISSUE.ISSUE_NAME";

       
        String queryCount = "SELECT Count(*) as issueId " 
				+ "FROM T_ISSUE, T_RAISSUE_LNK "
				+ "WHERE T_RAISSUE_LNK.FK_ISSUE_ID=T_ISSUE.PK_ISSUE_ID AND T_RAISSUE_LNK.FK_RA_ID= ? ";
        
        try {
    		
			Integer countObligation = jdbcTemplate.queryForObject(queryCount, Integer.class, IssueOblId);
		
			if (countObligation == 0) {

				return null;

			}else {
		
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Issue.class), IssueOblId);

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("The obligation you requested with id " + IssueOblId + " was not found in the database");
		}
	}
	@Override
	public List<Issue> findAllIssuesList(){
		String query = "SELECT T_ISSUE.ISSUE_NAME as issueName, T_ISSUE.PK_ISSUE_ID as issueId " 
				+ "FROM T_ISSUE "
				+ "ORDER BY T_ISSUE.ISSUE_NAME";

       
        String queryCount = "SELECT Count(*) as issueId " 
				+ "FROM T_ISSUE ";

        
        try {
    		
			Integer countIssues = jdbcTemplate.queryForObject(queryCount, Integer.class);
		
			if (countIssues == 0) {

				return null;

			}else {
		
				return jdbcTemplate.query(query, new BeanPropertyRowMapper<>(Issue.class));

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("The Issues not found in the database");
		}
	}
	
	
	@Override
	public Issue findById(Integer IssueId) {
		String query = "SELECT T_ISSUE.ISSUE_NAME as issueName, T_ISSUE.PK_ISSUE_ID as issueId " 
				+ "FROM T_ISSUE "
				+ "Where PK_ISSUE_ID = ?";

       
        String queryCount = "SELECT Count(*) as issueId " 
				+ "FROM T_ISSUE "
				+ "Where PK_ISSUE_ID = ?";

        
        try {
    		
			Integer countObligation = jdbcTemplate.queryForObject(queryCount, Integer.class, IssueId);
		
			if (countObligation == 0) {
				throw new ResourceNotFoundException("The Issue with id " + IssueId +"  not found in the database");
			}else {
		
				return jdbcTemplate.queryForObject(query, new BeanPropertyRowMapper<>(Issue.class), IssueId);

			}
			
		} catch (DataAccessException e) {
			throw new ResourceNotFoundException("The Issue with id " + IssueId +"  not found in the database");
		}
	}
	
}
