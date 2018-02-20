package eionet.rod.dao;

import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import eionet.rod.model.Delivery;
import eionet.rod.service.DeliveryService;
import eionet.rod.util.exception.ResourceNotFoundException;


/**
 * Test the issue dao.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})
@Sql("/seed-issue.sql")
public class ITDeliveryDao {
    
	@Autowired
	private DeliveryService deliveryService;
	
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
    public void testfindAllDelivery() throws ResourceNotFoundException
    {
    	List<Delivery> deliveries = deliveryService.getAllDelivery("1", "1");
    	System.out.println(deliveries.size());
    	//assertEquals("END - Austria - DF2 - 2015",deliveries.get(0).getDeliveryTitle());
   	
    }
    
    
}
