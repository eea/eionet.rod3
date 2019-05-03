package eionet.rod.dao;

import eionet.rod.service.RoleService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.Hashtable;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})

public class ITRoleDao {

    @Autowired
    private RoleService roleService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testSaveBackUpCommitRoles() {

        Hashtable<String, Object> role = new Hashtable<>();

        role.put("ID", "eionet-nfp");
        role.put("URL", "");
        role.put("URL_MEMBERS", "");
        role.put("DESCRIPTION", "National Focal Points");
        role.put("MAIL", "");

        roleService.saveRole(role);

        roleService.backUpRoles();

        roleService.commitRoles();

    }

}
