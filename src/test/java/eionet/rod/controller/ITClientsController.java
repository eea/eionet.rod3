package eionet.rod.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.*;

import org.junit.Before;

import org.junit.runner.RunWith;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
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

@Sql("/seed-clients.sql")
/**
 * Test the clients controller.
 */
public class ITClientsController {

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
     * Simple test to list clients.
     */
    @Test
    public void listClients() throws Exception {
        this.mockMvc.perform(get("/clients"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("allClients"))
                .andExpect(view().name("clients"));
    }

    /**
     * Simple test to display one client.
     */
    @Test
    public void listClient1() throws Exception {
        this.mockMvc.perform(get("/clients/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("client"))
                .andExpect(model().attributeExists("directObligations"))
                .andExpect(model().attributeExists("indirectObligations"))
                .andExpect(view().name("clientFactsheet"));
    }

    /**
     * Since it is protected, it will redirect to login.
     */
    @Test
    public void clientEdit() throws Exception {
    	this.mockMvc.perform(get("/clients/1/edit"))
            //.andExpect(status().isOk());
    		.andExpect(status().is3xxRedirection());
    }

    /**
     * Login and edit.
     */
    @Test
    public void clientEditWithAuth() throws Exception {
        this.mockMvc.perform(get("/clients/1/edit")
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("client"));
    }

    /**
     * Login and post an update.
     */
    @Test
    public void clientPostWithAuth() throws Exception {
        this.mockMvc.perform(post("/clients/1/edit")
                .param("clientId", "1")
                .param("name", "New Name")
                .with(csrf()).with(user("editor").roles("EDITOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients/1/edit?message=Client+1+updated"))
                .andExpect(model().attributeExists("message"));
    }


    /**
     * Login and post an update, but without CSRF.
     * CSRF is Cross Site Request Forgery prevention
     */
    @Test
    public void editPostWithoutCSRF() throws Exception {
        this.mockMvc.perform(post("/clients/1/edit")
                .param("clientId", "1")
                .param("name", "New Name")
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().is4xxClientError());
    }

    /**
     * Delete a client without login.
     */
    @Test
    public void deleteWithoutAuth() throws Exception {
        this.mockMvc.perform(get("/clients/delete/1")
                .param("clientId", "1"))
                .andExpect(status().is3xxRedirection());
    }


    /**
     * Login and delete a client
     */
    @Test
    public void deleteWithAuth() throws Exception {
        this.mockMvc.perform(get("/clients/delete/1")
                .param("clientId", "1")
                .with(csrf()).with(user("editor").roles("EDITOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/clients?message=Client+1+deleted"))
                .andExpect(model().attributeExists("message"));
    }
    
    /**
     * Login and delete a client
     */
    @Test
    public void deleteIds() throws Exception {
        this.mockMvc.perform(post("/clients/delete")
                .param("delClients", "1,")
                .with(csrf()).with(user("editor").roles("EDITOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("view?message=Clients+selected+deleted."))
                .andExpect(model().attributeExists("message"));
    }

    /**
     * Login and inject SQL - should fail
     */
    @Test(expected = org.springframework.web.util.NestedServletException.class)
    public void deleteIdInject() throws Exception {
        this.mockMvc.perform(post("/clients/delete")
                .param("delClients", "1; drop table t_client;")
                .with(csrf()).with(user("editor").roles("EDITOR")))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("view?message=Clients+selected+deleted."))
                .andExpect(model().attributeExists("message"));
    }
    

    /**
     * Delete a clients without login.
     */
    @Test
    public void deleteIdsWithoutAuth() throws Exception {
        this.mockMvc.perform(get("/clients/delete")
                .param("delClients", "1,"))
                .andExpect(status().is3xxRedirection());
    }

}
