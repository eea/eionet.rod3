package eionet.rod.controller;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.user;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

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

@Sql("/seed-obligation-source.sql")
/**
 * Test the Harvest controller.
 */
public class ITHarvesterController {

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
     * Simple test.
     */
    @Test
    public void viewHarvest() throws Exception {
        this.mockMvc.perform(get("/harvester"))
        		.andExpect(status().is3xxRedirection());
    }
    /**
    * Simple test.
    */
//   @Test
//   public void viewHarvestPost() throws Exception {
//	   this.mockMvc.perform(post("/harvester")
//               .param("mode", "0")
//               .with(csrf()).with(user("editor").roles("EDITOR")))
//               .andExpect(status().isOk())
//               .andExpect(model().attributeExists("message"));
//   }
//   
//   @Test
//   public void viewHarvestPostWithoutCSRF() throws Exception {
//	   this.mockMvc.perform(post("/harvester")
//               	.param("mode", "0")
//               	.with(user("editor").roles("EDITOR")))
//       			.andExpect(status().is4xxClientError());
//   }
   
   @Test
   public void viewHarvestPost_mode2() throws Exception {
	   this.mockMvc.perform(post("/harvester")
               .param("mode", "2")
               .with(csrf()).with(user("editor").roles("EDITOR")))
               .andExpect(status().isOk())
               .andExpect(model().attributeExists("message"));
   }

   @Test
   public void viewHarvestPostWithoutCSRF_mode2() throws Exception {
	   this.mockMvc.perform(post("/harvester")
               	.param("mode", "2")
               	.with(user("editor").roles("EDITOR")))
				.andExpect(status().is4xxClientError());
   }
	
}
