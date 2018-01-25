package eionet.rod.controller;

import static org.junit.Assert.assertTrue;

import javax.sql.DataSource;

import org.dbunit.DataSourceDatabaseTester;
import org.dbunit.IDatabaseTester;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.web.FilterChainProxy;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})


/**
 * Test the spatial controller.
 */
public class ITHelpController {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Autowired
    private FilterChainProxy springSecurityFilterChain;

    @Autowired
    private DataSource dataSource;

    private IDatabaseTester databaseTester;
  

    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(this.springSecurityFilterChain)
            .build();
        databaseTester = new DataSourceDatabaseTester(dataSource);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream("seed-help.xml"));
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

    @Test
    public void helpfindId() throws Exception  {
    	RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
				"/help.do").param("helpId", "HELP_LI_ABSTRACT");
    	MvcResult result;
		result = mockMvc.perform(requestBuilder).andReturn();
		String expectedText ="The focus should be on defining what";
		assertTrue(result.getResponse().getContentAsString().contains(expectedText));
		//System.out.println("result help: " + result.getResponse().getContentAsString());
    }
}
