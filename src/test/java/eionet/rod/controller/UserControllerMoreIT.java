package eionet.rod.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.UserRequestPostProcessor.*;
//import static org.hamcrest.Matchers.*;

import eionet.rod.model.UserRole;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

/**
 * Test the simple doc controller.
 */
public class UserControllerMoreIT {

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
     * Test Edit user form page.
     */
    @Test
    public void addAndEditUser() throws Exception {
        String username = "testuser";
        String roleName = UserRole.ROLE_EDITOR.name();

        this.mockMvc.perform(get("/users/add").with(user("admin").roles("ADMIN"))
                .param("userId", username)
                .param("authorisations", roleName))
                .andExpect(status().is3xxRedirection());

        this.mockMvc.perform(get("/users/edit").with(user("admin").roles("ADMIN"))
                .param("userName", username))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("userName"))
                .andExpect(model().attributeExists("user"))
                .andExpect(view().name("userEditForm"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

}
