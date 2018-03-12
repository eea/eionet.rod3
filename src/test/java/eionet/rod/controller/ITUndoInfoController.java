package eionet.rod.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-undo.sql")

public class ITUndoInfoController {

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
    
    @Test
    public void viewObligationUndoInfo() throws Exception {
    	this.mockMvc.perform(get("/undoinfo")
    	.param("ts", "1519895729320")
    	.param("tab", "T_OBLIGATION")
    	.param("op", "U")
    	.param("id", "1")
    	.param("user", "arroyyol"))
    	.andExpect(status().isOk())
		.andExpect(model().attributeExists("time"))
        .andExpect(model().attributeExists("user"))
        .andExpect(model().attributeExists("table"))
        .andExpect(model().attributeExists("operation"))
        .andExpect(model().attributeExists("undoList"))
        .andExpect(model().attributeExists("undoCountries"))
        .andExpect(model().attributeExists("undoCountriesVol"))
        .andExpect(model().attributeExists("currentCountries"))
        .andExpect(model().attributeExists("currentCountriesVol"))
        .andExpect(model().attributeExists("addedCountries"))
        .andExpect(model().attributeExists("removedCountries"))
        .andExpect(model().attributeExists("addedCountriesVol"))
        .andExpect(model().attributeExists("removedCountriesVol"))
        .andExpect(model().attributeExists("undoIssues"))
        .andExpect(model().attributeExists("currentIssues"))
        .andExpect(model().attributeExists("addedIssues"))
        .andExpect(model().attributeExists("removedIssues"))
        .andExpect(model().attributeExists("undoClients"))
        .andExpect(model().attributeExists("currentClients"))
        .andExpect(model().attributeExists("addedClients"))
        .andExpect(model().attributeExists("removedClients"))
        .andExpect(model().attributeExists("undoObligations"))
        .andExpect(model().attributeExists("currentObligations"))
        .andExpect(model().attributeExists("addedObligations"))
        .andExpect(model().attributeExists("removedObligations"))
    	.andExpect(model().attributeExists("activeTab"))
    	.andExpect(model().attributeExists("currentValues"))
    	.andExpect(model().attributeExists("title"))
    	.andExpect(view().name("undoinfo"));
    }
	
}
