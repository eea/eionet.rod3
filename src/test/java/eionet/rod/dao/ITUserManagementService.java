package eionet.rod.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ITUserManagementService {

    private UserDetailsManager userManagementService;

    private ClassPathXmlApplicationContext ctx;

    @Before
    public void loadContext() {
        ctx = new ClassPathXmlApplicationContext("spring-db-config.xml");

        userManagementService = ctx.getBean("userService", UserManagementService.class);
    }

    @After
    public void closeContext() {
        ctx.close();
    }

    @Test
    public void simpleTest() throws Exception {

        UserDetails user = userManagementService.loadUserByUsername("anyuser");

        assertNotNull(user);
        assertEquals("anyuser", user.getUsername());

    }

    @Test
    public void productionTest() throws Exception {
        InitialUser initialUser = new InitialUser();
        initialUser.setInitialUsername("johnny");
        initialUser.setInitialPassword("rotten");
        initialUser.setUserManagementService((UserManagementService) userManagementService);
        initialUser.createUser();
    }

}
