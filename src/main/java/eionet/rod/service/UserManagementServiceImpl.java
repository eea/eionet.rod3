package eionet.rod.service;

import eionet.rod.dao.UserManagementDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service(value = "userManagementService")
@Transactional
public class UserManagementServiceImpl implements UserManagementService {

    @Autowired
    UserManagementDao userManagementDao;

    @Override
    public List<UserDetails> getAllUsers() {
        return userManagementDao.getAllUsers();
    }

    @Override
    public void createUser(UserDetails userDetails) {
        userManagementDao.createUser(userDetails);
    }

    @Override
    public void updateUser(UserDetails userDetails) {
        userManagementDao.updateUser(userDetails);
    }

    @Override
    public void deleteUser(String userName) {
        userManagementDao.deleteUser(userName);
    }

    @Override
    public boolean userExists(String userName) {
        return userManagementDao.userExists(userName);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        return userManagementDao.loadUserByUsername(userName);
    }
}
