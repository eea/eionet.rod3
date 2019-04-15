package eionet.rod.dao;

import java.util.Hashtable;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.dao.DataAccessException;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.util.exception.ResourceNotFoundException;

@Repository
@Transactional
public class RoleDaoImpl implements RoleDao {
	
	private static final Log logger = LogFactory.getLog(RoleDaoImpl.class);
	
	public RoleDaoImpl() {
	}
	
	private JdbcTemplate jdbcTemplate;
	
	@Resource
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	private static final String q_commit_roles = "DELETE FROM T_ROLE WHERE STATUS=0";

    @Override
    public void commitRoles() {
    	try {
    		jdbcTemplate.update(q_commit_roles);
    	
    	 } catch (DataAccessException e) {
    		 logger.error("Commit roles exception", e);
 			throw new ResourceNotFoundException("DataAccessException error: " + e.getMessage());
       } 
    	
    }
    
    private static final String q_update_role_status = "UPDATE T_ROLE " + "SET STATUS=?";

    @Override
    public void backUpRoles() {
    	try {
    		jdbcTemplate.update(q_update_role_status,0);
    	
    	 } catch (DataAccessException e) {
    		 logger.error("Commit roles exception", e);
 			throw new ResourceNotFoundException("DataAccessException error: " + e.getMessage());
       } 

    }
    
    private static final String q_delete_role_data =
            "DELETE FROM T_ROLE "
            + "WHERE ROLE_ID=? AND STATUS=1";

        private static final String q_insert_role =
            "INSERT INTO T_ROLE "
            + "SET STATUS=?, LAST_HARVESTED={fn now()}, ROLE_NAME=?, ROLE_EMAIL=?, ROLE_ID=?, ROLE_URL=?, ROLE_MEMBERS_URL=?";

        private static final String q_update_role_rollback =
            "UPDATE T_ROLE "
            + "SET STATUS=1 "
            + "WHERE STATUS=0 AND ROLE_ID=?";
    
    @Override
    public void saveRole(Hashtable<String, Object> role) {
        String roleId = (String) role.get("ID");
       

        try {
        	jdbcTemplate.update(q_delete_role_data,roleId);
           

            String circaUrl = (String) role.get("URL");
            String circaMembersUrl = (String) role.get("URL_MEMBERS");
            String desc = (String) role.get("DESCRIPTION");
            String mail = (String) role.get("MAIL");

            if (roleId != null) {
            	jdbcTemplate.update(q_insert_role,1,desc, mail, roleId, circaUrl, circaMembersUrl);
            	
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            try {
            	jdbcTemplate.update(q_delete_role_data, roleId);
               
            	jdbcTemplate.update(q_update_role_rollback, roleId);

            }catch (DataAccessException ex) {
            	logger.error(ex.getMessage(), ex);
    			throw new ResourceNotFoundException("DataAccessException error: " + ex.getMessage());
    		} 
            
       }
    }

    
}
