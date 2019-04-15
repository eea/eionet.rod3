package eionet.rod.controller;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;

import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import org.springframework.http.MediaType;

import static org.hamcrest.MatcherAssert.assertThat;



@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-spatial.sql")
/**
 * Test the spatial Api controller.
 */
public class ITSpatialApiController {

   	@Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private DataSource dataSource;

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
    public void listAllCountries() throws Exception {
    	RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/api/spatial").accept(
				MediaType.APPLICATION_JSON);
    	
    	MvcResult result = mockMvc.perform(requestBuilder).andReturn();
    	
        String expected = "{\"spatialId\":1,\"name\":\"Austria\",\"type\":\"C\",\"twoLetter\":\"AT\",\"memberCountry\":\"Y\"}";
                      
       // System.out.println("result by All: " + result.getResponse().getContentAsString());

        assertThat(result.getResponse().getContentAsString(), !result.getResponse().getContentAsString().isEmpty());
        
        assertFalse(result.getResponse().getContentAsString().contentEquals(expected));
       
    }
     

	/**
     * Simple test to display one country by ID.
     */
    @Test
    public void listSpatialID() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/api/spatial/1").accept(
				MediaType.APPLICATION_JSON);

		MvcResult result = mockMvc.perform(requestBuilder).andReturn();

		//System.out.println("result by ID: " + result.getResponse().getContentAsString());
		String expected = "{\"spatialId\":1,\"name\":\"Austria\",\"type\":\"C\",\"twoLetter\":\"AT\",\"memberCountry\":\"Y\"}";

		assertEquals(expected, result.getResponse().getContentAsString());
        
    }
    
}
