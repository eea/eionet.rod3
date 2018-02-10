package eionet.rod.dao;

import org.junit.runner.RunWith;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import eionet.rod.model.Issue;

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
public class ITIssueDao {
    
	@Autowired
	private IssueDao issueDao;
	
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
		
    @Test
    public void testfindObligationIssuesList() throws ResourceNotFoundException
    {
    	List<Issue> issues = issueDao.findObligationIssuesList(1);
    	assertEquals("Climate Change",issues.get(0).getIssueName());
    	assertEquals("1",issues.get(0).getIssueId().toString());
    	issues = issueDao.findObligationIssuesList(12);
    	assertNull(issues);
    	
    }
    
    
}
