package eionet.rod.controller;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test the search controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-obligation-source.sql")

public class ITSearchController {

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
     * Test front Search home.
     */
    @Test
    public void simpleSearchHome() throws Exception {
        this.mockMvc.perform(get("/simpleSearch"))
                .andExpect(status().isOk())
                .andExpect(view().name("simpleSearch"))
                .andExpect(content().contentType("text/html;charset=UTF-8"));
    }

    /**
     * Test Search simple.
     */
    @Test
    public void simpleSearch() throws Exception {
        this.mockMvc.perform(post("/simpleSearch")
                .param("expression", "water")
                .param("execute", "GO")
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
        //.andExpect(view().name("simpleSearch"))
    }

    @Test
    public void simpleSearchWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/simpleSearch")
                .param("expression", "water")
                .param("execute", "GO"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void advancedSearchHome() throws Exception {
        this.mockMvc.perform(get("/advancedSearch"))
                .andExpect(status().isOk())
                .andExpect(view().name("search"));
    }

    @Test
    public void advancedSearch() throws Exception {
        this.mockMvc.perform(post("/advancedSearch")
                .param("spatialId", "0")
                .param("issueId", "NI")
                .param("clientId", "0")
                .param("nextDeadlineFrom", "")
                .param("nextDeadlineTo", "")
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void advancedSearchWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/advancedSearch")
                .param("spatialId", "0")
                .param("issueId", "NI")
                .param("clientId", "0")
                .param("nextDeadlineFrom", "")
                .param("nextDeadlineTo", ""))
                .andExpect(status().is4xxClientError());

    }

}
