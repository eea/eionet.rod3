package eionet.rod.controller;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
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

//import eionet.rod.model.Obligations;
import eionet.rod.model.Spatial;
import eionet.rod.service.ObligationService;
import eionet.rod.service.SpatialService;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-spatial.sql")
/**
 * Test the spatial controller.
 */
public class ITSpatialController {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private DataSource datasource;

    @Autowired
    SpatialService spatialService;

    @Autowired
    ObligationService obligationService;
    
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
    public void listSpatial() throws Exception {
    	
    	List<Spatial> spatialListY = new ArrayList<>();
    	spatialListY.add(new Spatial(1, "Austria", "C", "AT", "Y"));
    	spatialListY.add(new Spatial(3, "Francia", "C", "FR", "Y"));
    	
    	List<Spatial> spatialListN = new ArrayList<>();
    	spatialListN.add(new Spatial(2, "Albania", "C", "AL", "N"));
    	
    	
        this.mockMvc.perform(get("/spatial"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("allMember"))
                .andExpect(model().attributeExists("allNoMember"))
                .andExpect(model().attributeExists("title"))
                .andExpect(view().name("spatial"));

        List<Spatial> val = spatialService.findAllMember("Y");
        assertEquals(spatialListY.get(0).getName(), val.get(0).getName());
      
        List<Spatial> valN = spatialService.findAllMember("N");
        assertEquals(spatialListN.get(0).getName(), valN.get(0).getName());
        
    }
    
      
    @Test
    public void testspatial_deadlines() throws Exception {
        this.mockMvc.perform(get("/spatial/1/deadlines")
                .param("spatialId", "1"))
        		.andExpect(status().isOk())
        		.andExpect(view().name("deadlines"));
    }
    
    @Test
    public void testspatial_search_deadlines() throws Exception 
    {
    	this.mockMvc.perform(post("/spatial/1/deadlines/search")
    			.param("issueId", "0")
    			.param("deadlineId", "0")
    			.param("clientId", "0")
    			.with(csrf()))
    			.andExpect(status().isOk())
        		.andExpect(view().name("deadlines"));
    }
    
    @Test
    public void testspatial_search_deadlinesWithoutCsrf() throws Exception 
    {
    	this.mockMvc.perform(post("/spatial/1/deadlines/search")
    			.param("issueId", "0")
    			.param("deadlineId", "0")
    			.param("clientId", "0"))
				.andExpect(status().is4xxClientError());
    }
    
}
