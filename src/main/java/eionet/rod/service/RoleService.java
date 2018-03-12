package eionet.rod.service;

import java.util.Hashtable;

public interface RoleService {

	void commitRoles();
	 
	void backUpRoles();
	
	void saveRole(Hashtable<String, Object> role);
}
