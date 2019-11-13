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
 * Test the intruments controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-obligation-source.sql")
public class ITInstrumentsController {

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
     * Simple test to view list of the instruments.
     */
    @Test
    public void viewInstruments() throws Exception {
        this.mockMvc.perform(get("/instruments"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(view().name("instruments"));
    }

    @Test
    public void sourceFactsheet() throws Exception {
        this.mockMvc.perform(get("/instruments/1")
                .param("sourceId", "1"))
                .andExpect(status().isOk());
    }

    @Test
    public void editInstrumentForm() throws Exception {
        this.mockMvc.perform(get("/instruments/edit?sourceId=1")
                .with(user("editor").roles("EDITOR"))
                .param("sourceId", "1"))
                .andExpect(status().isOk())
                .andExpect(view().name("instrumentEditForm"));

    }

    @Test
    public void addInstrumentForm() throws Exception {
        this.mockMvc.perform(get("/instruments/add")
                .with(user("editor").roles("EDITOR")))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("title"))
                .andExpect(model().attributeExists("activeTab"))
                .andExpect(view().name("instrumentEditForm"));
    }

    @Test
    public void addInstrument() throws Exception {
        this.mockMvc.perform(post("/instruments/add")
                .param("sourceId", "4")
                .param("sourceTitle", "Test Yoly")
                .param("sourceAlias", "Test Yoly")
                .param("sourceCode", "2222")
                .param("sourceTerminate", "N")
                .param("SourceUrl", "https://www.google.es")
                .param("sourceLnkFKSourceParentId", "1")
                .param("SourceCelexRef", "31977D0795")
                .param("clientId", "2")
                .param("sourceValidFrom", "")
                .param("sourceFkTypeId", "15")
                .param("sourceEcEntryIntoForce", "")
                .param("SourceEcAccession", "")
                .param("selectedClassifications", "1")
                .with(user("editor").roles("EDITOR"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

    }

    @Test
    public void deleteInstrumentsWithCsrf() throws Exception {
        this.mockMvc.perform(post("/instruments/delete")
                .param("sourceId", "1")
                .with(user("editor").roles("ADMIN"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection());

    }

    /**
     * Tests that editors cannot delete instruments
     * @throws Exception
     */
    @Test
    public void deleteInstrumentsWithCsrfEditor() throws Exception {
        this.mockMvc.perform(post("/instruments/delete")
                .param("sourceId", "1")
                .with(user("editor").roles("EDITOR"))
                .with(csrf()))
                .andExpect(status().is4xxClientError());

    }

    @Test
    public void deleteInstrumentsWithoutCsrf() throws Exception {
        this.mockMvc.perform(post("/instruments/delete")
                .param("sourceId", "1")
                .with(user("editor").roles("ADMIN")))
                .andExpect(status().is4xxClientError());

    }


    @Test
    public void editInstrument() throws Exception {
        this.mockMvc.perform(post("/instruments/edit")
                .param("sourceId", "1")
                .param("hrefInstruments", "/rod/instruments")
                .param("sourceTitle", "Basel Convention on the control of transboundary movements of hazardous wastes and their disposal")
                .param("sourceAlias", "Basel Convention")
                .param("sourceCode", "")
                .param("sourceUrl", "")
                .param("sourceCelexRef", "")
                .param("clientId", "1")
                .param("sourceIssuedByUrl", "")
                .param("sourceLnkFKSourceParentId", "1")
                .param("_selectedClassifications", "1")
                .param("selectedClassifications", "1")
                .param("selectedClassifications", "2")
                .param("sourceValidFrom", "")
                .param("sourceAbstract", "")
                .param("sourceComment", "")
                .param("SourceEcAccession", "")
                .param("sourceEcEntryIntoForce", "")
                .param("sourceSecretariat", "")
                .param("sourceSecretariatUrl", "")
                .param("edit", "Save changes")
                .with(user("editor").roles("EDITOR"))
                .with(csrf()))
                .andExpect(status().is3xxRedirection());
    }


}
