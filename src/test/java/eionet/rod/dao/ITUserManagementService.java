package eionet.rod.dao;
 
import java.util.List;
 
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.UserDetailsManager;
 
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

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

}
