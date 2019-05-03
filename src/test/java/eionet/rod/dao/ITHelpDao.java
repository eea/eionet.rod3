package eionet.rod.dao;

import eionet.rod.model.Help;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;


/**
 * Test the spatial dao.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})
@Sql("/seed-help.sql")
public class ITHelpDao {


    @Autowired
    private HelpDao helpDao;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testHelpfinId() throws ResourceNotFoundException {
        Help help = helpDao.findId("HELP_LI_ABSTRACT");

        String expectedTitle = "Legislative instrument";
        String expectedText = "The focus should be on defining what";

        assertNotNull(help);
        assertTrue(help.getText().contains(expectedText));
        assertTrue(help.getTitle().contains(expectedTitle));
        //System.out.println("Help Text: " + help.getText());
        //System.out.println("Help Title: " + help.getTitle());
    }


}
