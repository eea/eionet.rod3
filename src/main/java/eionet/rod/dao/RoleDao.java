package eionet.rod.dao;

import java.util.Hashtable;

public interface RoleDao {

    void commitRoles();

    void backUpRoles();

    void saveRole(Hashtable<String, Object> role);
}
