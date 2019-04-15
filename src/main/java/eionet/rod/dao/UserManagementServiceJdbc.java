package eionet.rod.dao;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.HashSet;
import java.util.Set;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;


/**
 * Extends Spring's JDBC implementation to add a list of all users and accept users thart are not in the database.
 */
public class UserManagementServiceJdbc extends JdbcUserDetailsManager
        implements UserManagementService {

    private static final String DEF_GET_ALL_USERS_SQL = "SELECT * FROM users ORDER BY username";

    /** The users table has only the people with roles, so we fake the check as we're using CAS. */
    @Override
    public UserDetails loadUserByUsername(String username) {
        Set<GrantedAuthority> dbAuthsSet = new HashSet<>();

        if (getEnableAuthorities()) {
            dbAuthsSet.addAll(loadUserAuthorities(username));
        }

        List<GrantedAuthority> dbAuths = new ArrayList<>(dbAuthsSet);
        return new User(username, "", true, true, true, true, dbAuths);
    }

    @Override
    public List<UserDetails> getAllUsers() {
        return getJdbcTemplate().query(DEF_GET_ALL_USERS_SQL, new String[] {},
                    new RowMapper<UserDetails>() {
                        public UserDetails mapRow(ResultSet rs, int rowNum) throws SQLException {
                            String username = rs.getString(1);
                            String password = rs.getString(2);
                            boolean enabled = rs.getBoolean(3);
                            return new User(username, password, enabled, true, true, true, loadUserAuthorities(username));
                        }

                    });
    }

}
