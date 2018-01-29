package eionet.rod.controller;

//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import javax.sql.DataSource;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
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
 * Test the Obligations controller.
 */
public class ITObligationsController {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private DataSource datasource;

    private IDatabaseTester databaseTester;

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(this.springSecurityFilterChain)
            .build();
        databaseTester = new DataSourceDatabaseTester(datasource);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream("seed-obligation.xml"));
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
       
    }
           
    /**
     * Simple test to list countries.
     */
    @Test
    public void listObligations() throws Exception {
        this.mockMvc.perform(get("/obligations"))
                .andExpect(status().isOk())
                .andExpect(view().name("obligations"));
       
    }
    
    
    /**
     * Simple test to display one obligation by ID.
     */
    @Test
    public void viewObligationID() throws Exception {
    	 this.mockMvc.perform(get("/obligations/1"))
         	.andExpect(status().isOk())
         	.andExpect(model().attributeExists("obligation"))
        	.andExpect(model().attributeExists("breadcrumbs"))
         	.andExpect(model().attributeExists("title"));

        
    }
    
    /**
     * Simple test to display legislation by obligationID.
     */
    @Test
    public void viewLegislation() throws Exception {
    	 this.mockMvc.perform(get("/obligations/1/legislation"))
         	.andExpect(status().isOk())
         	.andExpect(model().attributeExists("obligationId"))
         	.andExpect(model().attributeExists("obligation"))
         	.andExpect(model().attributeExists("breadcrumbs"))
          	.andExpect(model().attributeExists("issues"))
         	.andExpect(model().attributeExists("ObligationCountries"))
         	.andExpect(model().attributeExists("title"));
        
    }
    
    
    @Test
    public void testaddObligation() throws Exception
    {
    	this.mockMvc.perform(get("/obligations/add"))
    		.andExpect(status().isOk());

    }

}
