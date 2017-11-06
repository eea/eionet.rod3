package eionet.rod.dao;

import java.util.List;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.GroupManager;
import org.springframework.security.provisioning.UserDetailsManager;

/**
 * Additional operations for Springs UserDetailsManager.
 */
public interface UserManagementService extends UserDetailsManager, GroupManager {

    /**
     * Returns a list of all users.
     *
     * @return list of users
     */
    List<UserDetails> getAllUsers();

}
