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
    public void simpleSearchWithoutCsrf() throws Exception
	{
  		this.mockMvc.perform(post("/simpleSearch")
          		.param("expression", "water")
          		.param("execute", "GO"))
        		.andExpect(status().is4xxClientError());

    }
    
}
