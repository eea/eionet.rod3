package eionet.rod.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.List;

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
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import org.springframework.web.context.WebApplicationContext;

import eionet.rod.model.Spatial;
import eionet.rod.service.SpatialService;

import static org.junit.Assert.assertEquals;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})


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

    private IDatabaseTester databaseTester;

    @Autowired
    SpatialService spatialService;

   
    
    @Before
    public void setUp() throws Exception {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(this.springSecurityFilterChain)
            .build();
        databaseTester = new DataSourceDatabaseTester(datasource);
        IDataSet dataSet = new FlatXmlDataSetBuilder().build(getClass().getClassLoader().getResourceAsStream("seed-spatial.xml"));
        databaseTester.setDataSet(dataSet);
        databaseTester.onSetup();
    }

      
    /**
     * Simple test to list countries.
     */
    @Test
    @Rollback(true)
    public void listSpatial() throws Exception {
    	
    	List<Spatial> spatialListY = new ArrayList<Spatial>();
    	spatialListY.add(new Spatial(1, "Austria", "C", "AT", "Y"));
    	spatialListY.add(new Spatial(3, "Francia", "C", "FR", "Y"));
    	
    	List<Spatial> spatialListN = new ArrayList<Spatial>();
    	spatialListN.add(new Spatial(2, "Albania", "C", "AL", "N"));
    	
    	
        this.mockMvc.perform(get("/spatial"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("breadcrumbs"))
                .andExpect(model().attributeExists("allMember"))
                .andExpect(model().attributeExists("allNoMember"))
                .andExpect(model().attributeExists("allMemberColumn1"))
                .andExpect(model().attributeExists("allMemberColumn2"))
                .andExpect(model().attributeExists("allMemberColumn3"))
                .andExpect(model().attributeExists("allNoMemberColumn1"))
                .andExpect(model().attributeExists("allNoMemberColumn2"))
                .andExpect(model().attributeExists("allNoMemberColumn3"))
                .andExpect(model().attributeExists("title"))
                .andExpect(view().name("spatial"));

        List<Spatial> val = spatialService.findAllMember("Y");
        assertEquals(spatialListY.get(0).getName(), val.get(0).getName());
      
        List<Spatial> valN = spatialService.findAllMember("N");
        assertEquals(spatialListN.get(0).getName(), valN.get(0).getName());
        
    }
}
