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
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.junit.Assert.assertTrue;

/**
 * Test the help controller.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-help.sql")
public class ITHelpController {

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
    public void helpfindId() throws Exception {
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "/help.do").param("helpId", "HELP_LI_ABSTRACT");
        MvcResult result;
        result = mockMvc.perform(requestBuilder).andReturn();
        String expectedText = "The focus should be on defining what";
        assertTrue(result.getResponse().getContentAsString().contains(expectedText));
        //System.out.println("result help: " + result.getResponse().getContentAsString());
    }
}
