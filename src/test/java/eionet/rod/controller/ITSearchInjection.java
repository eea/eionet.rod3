package eionet.rod.controller;

import eionet.rod.model.Obligations;
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
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotEquals;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Test for security vulnerabilities.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

@Sql("/seed-obligation-source.sql")
@Sql("/seed-users.sql")
public class ITSearchInjection {

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
     * Investigate SQL injection.
     * The /advancedSearch page doesn't check its input, nor does it use data types properly
     * for date values. It would therefore be easy for an attacker to construct a script
     * that put something else than a date into the 'nextDeadlineTo' parameter and see anything
     * in the database or perform any function.
     * <p>
     * This proof of concept fetches the username and password from the 'users' table and displays
     * those instead of the obligations as the result.
     * To see the actual generated HTML page, uncomment the andDo(print()) statement.
     * <p>
     * In a correct implementation the result should be a code 4XX and the SQL should not have run.
     */
    @Test
    @SuppressWarnings("unchecked")
    public void injectInNextDeadlineTo() throws Exception {
        String date2 = "01/03/1900-01-31')) UNION SELECT 1,username,2,0,'',3,'2000-01-01',4,password,6,7,8,9,10,11 FROM users; -- ";
        MvcResult res = this.mockMvc.perform(post("/advancedSearch")
                .param("spatialId", "0")
                .param("issueId", "NI")
                .param("clientId", "0")
                .param("nextDeadlineFrom", "")
                .param("nextDeadlineTo", date2)
                .with(csrf()))
                //.andDo(print())
                .andReturn();
        ModelAndView mv = res.getModelAndView();
        if (mv == null) {
            return;
        }
        Map<String, Object> model = mv.getModel();
        if (model == null) {
            return;
        }
        List<Obligations> allObligations = (List<Obligations>) model.get("allObligations");

        // No "allObligations" in model means this issue is fixed
        if (allObligations == null) {
            return;
        }
        for (Obligations o : allObligations) {
            //System.out.println("!!! " + o.getOblTitle() + ":" + o.getClientName());
            assertNotEquals("SQL injection: Found 'mypassword' in output", "mypassword", o.getClientName());
            //assertFalse("SQL injection: Found 'anotherpassword' in output", "anotherpassword".equals(o.getClientName()));
        }
    }


    /**
     * The original alert from the EU Computer Emergency Response Team for ROD2.
     * Their proof of concept was specific to MySQL, and kept as is.
     * <p>
     * This gets blocked at the controller level now.
     */
    @Test
    public void injectInstrumentsList() throws Exception {
        String idParam = "-15 UNION SELECT 1,2,make_set(6,@:=0x0a,(select(1)"
                + "from(information_schema.columns) SELECT where@:="
                + "make_set(511,@,0x3c6c693e,table_name,column_name)),@),4,5"
                + " SELECT ,6,7,8--";
        this.mockMvc.perform(get("/instruments")
                .param("id", idParam))
                .andExpect(status().is4xxClientError());
    }
}
