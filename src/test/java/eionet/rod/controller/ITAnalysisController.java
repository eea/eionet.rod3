package eionet.rod.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

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
public class ITAnalysisController {

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
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream("seed-obligation-source.xml"));
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
       
    }

    @Test
    public void analysisHome() throws Exception  {
    	 this.mockMvc.perform(get("/analysis"))
         	.andExpect(status().isOk())
         	.andExpect(view().name("analysis"));
    }
}
