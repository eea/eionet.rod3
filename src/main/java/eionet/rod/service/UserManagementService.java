package eionet.rod.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;

public interface UserManagementService {

    List<UserDetails> getAllUsers();

    void createUser(UserDetails userDetails);

    void updateUser(UserDetails userDetails);

    void deleteUser(String userName);

    boolean userExists(String userName);

    UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException;
}
