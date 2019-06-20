package eionet.rod.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test the simple doc controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})
public class SimpleDocControllerIT {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
                .addFilters(this.springSecurityFilterChain)
                .build();
    }

    /**
     * Test front page.
     */
    //@Test
    public void testIndex() throws Exception {
        this.mockMvc.perform(get("/"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(view().name("index"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    /**
     * Test about page. Overkill with authentication.
     */
    @Test
    public void testAbout() throws Exception {
        this.mockMvc.perform(get("/about").with(user("admin")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("title"))
                .andExpect(view().name("about"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    /**
     * Test login page.
     */
    @Test
    public void getLoginWithNoAuth() throws Exception {
        this.mockMvc.perform(get("/login"))
                .andExpect(status().is3xxRedirection());
    }

    /**
     * We're getting the login page, but we are already logged in.
     * What happens?
     */
    //  @Test
    public void getLogin() throws Exception {
        this.mockMvc.perform(get("/login").with(user("admin")))
                .andExpect(status().isOk());
        //.andExpect(model().attributeExists("breadcrumbs"))
        //.andExpect(status().is3xxRedirection());
        //.andExpect(view().name("index"));
        // .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    /**
     * Test logout page.
     */
    @Test
    public void testLogout() throws Exception {
        this.mockMvc.perform(get("/logout"))
                .andExpect(status().isOk())
                .andExpect(view().name("logout_all_apps"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }
}
