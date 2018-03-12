package eionet.rod.service;

import java.util.Hashtable;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.dao.RoleDao;

@Service(value = "roleService")
@Transactional
public class RoleServiceImpl implements RoleService {

	@Autowired
	private RoleDao roleDao;
	
	@Override
    public void commitRoles() {
		roleDao.commitRoles();
	}
	
	@Override
	public void backUpRoles() {
		roleDao.backUpRoles();
	}
	
	@Override
    public void saveRole(Hashtable<String, Object> role) {
		roleDao.saveRole(role);
	}
	
}
