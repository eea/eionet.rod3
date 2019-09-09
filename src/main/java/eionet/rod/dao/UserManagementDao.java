package eionet.rod.dao;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;

import java.util.List;

/**
 * Additional operations for Springs UserDetailsManager.
 */
public interface UserManagementDao extends UserDetailsManager, GroupManager {

    /**
     * Returns a list of all users.
     *
     * @return list of users
     */
    List<UserDetails> getAllUsers();

}
