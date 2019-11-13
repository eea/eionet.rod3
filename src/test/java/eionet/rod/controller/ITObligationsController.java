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
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Test the Obligations controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-obligation-source.sql")
public class ITObligationsController {

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
     * Simple test to list countries.
     */
    @Test
    public void viewObligations() throws Exception {
        this.mockMvc.perform(get("/obligations"))
                .andExpect(status().isOk())
                .andExpect(view().name("obligations"));

    }


    /**
     * Simple test to display one obligation by ID.
     */
    @Test
    public void obligation_overview() throws Exception {
        this.mockMvc.perform(get("/obligations/1"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("title"))
                .andExpect(view().name("obligation_overview"));


    }

    /**
     * Simple test to display legislation by obligationID.
     */
    @Test
    public void viewLegislation() throws Exception {
        this.mockMvc.perform(get("/obligations/1/legislation"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("obligation"))
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("issues"))
                .andExpect(model().attributeExists("ObligationCountries"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(view().name("obligation_legislation"));

    }

    /**
     * Simple test to display legislation by obligationID.
     */
    @Test
    public void viewDelivery() throws Exception {
        this.mockMvc.perform(get("/obligations/1/deliveries"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("obligation"))
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("deliveries"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(view().name("obligation_deliveries"));

    }


    @Test
    public void searchObligation() throws Exception {
        this.mockMvc.perform(post("/obligations/search")
                .param("spatialId", "0")
                .param("issueId", "NI")
                .param("clientId", "0")
                .param("_terminate", "on")
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(view().name("obligations"));
    }

    @Test
    public void editClientForm() throws Exception {
        this.mockMvc.perform(get("/obligations/1/edit")
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(view().name("eobligation"));
    }

    @Test
    public void obligation_add() throws Exception {
        this.mockMvc.perform(get("/obligations/add/1")
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(view().name("eobligation"));
    }


    @Test
    public void addObligation() throws Exception {
        this.mockMvc.perform(post("/obligations/add")
                .param("id", "add")
                .param("sourceId", "1")
                .param("clientId", "1")
                .param("oblTitle", "Test Yoly")
                .param("description", "Description yoly")
                .param("reportFreqMonths", "25")
                .with(user("editor").roles("EDITOR"))
                .with(csrf()))
                .andExpect(status().isOk());
        //.andExpect(redirectedUrl("/obligations/edit"));
    }


    @Test
    public void addObligationWithooutCsrf() throws Exception {
        this.mockMvc.perform(post("/obligations/add")
                .param("id", "add")
                .param("sourceId", "1")
                .param("clientId", "1")
                .param("oblTitle", "Test Yoly")
                .param("description", "Description yoly")
                .param("reportFreqMonths", "25")
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void editObligation() throws Exception {
        this.mockMvc.perform(post("/obligations/edit")
                .param("id", "edit")
                .param("obligationId", "1")
                .param("sourceId", "1")
                .param("clientId", "1")
                .param("oblTitle", "Test Yoly")
                .param("description", "Description yoly")
                .param("reportFreqMonths", "25")
                .param("firstReporting", "31/12/9999")
                .param("nextReporting", "")
                .param("responsibleRole", "extranet-aqipr-val")
                .param("validTo", "31/12/9999")
                .param("nextDeadline", "")
                .param("nextDeadline2", "")
                .param("terminate", "N")
                .with(user("editor").roles("EDITOR"))
                .with(csrf()))
                .andExpect(status().isOk());
        //.andExpect(view().name("eobligation"));
    }

    @Test
    public void editObligationWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/obligations/edit")
                .param("id", "edit")
                .param("obligationId", "1"))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void deleteObligationsWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/obligations/delete")
                .param("spatialId", "0")
                .param("issueId", "0")
                .param("clientId", "0")
                .param("hrefdelete", "/rod/obligations/")
                .param("delObligations", "1"))
                .andExpect(status().is4xxClientError());

    }

    public void deleteObligationsWithCsrf() throws Exception {
        this.mockMvc.perform(post("/obligations/delete")
                .param("spatialId", "0")
                .param("issueId", "0")
                .param("clientId", "0")
                .param("hrefdelete", "/rod/obligations/")
                .param("delObligations", "1")
                .with(user("editor").roles("ADMIN"))
                .with(csrf()))
                .andExpect(status().isOk());
    }

    @Test
    public void deleteObligation() throws Exception {
        this.mockMvc.perform(get("/obligations/delete/1")
                .with(user("editor").roles("ADMIN")))
                .andExpect(status().is3xxRedirection());
    }

}
