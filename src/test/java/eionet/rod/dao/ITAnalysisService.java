package eionet.rod.dao;

import eionet.rod.model.AnalysisDTO;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import static org.junit.Assert.assertEquals;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})
@Sql("/seed-obligation-source.sql")
public class ITAnalysisService {

    @Autowired
    private AnalysisService analysisService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testGetStatistics() {

        AnalysisDTO analysisDTORec = analysisService.getStatistics();
        assertEquals("2", analysisDTORec.getTotalRa().toString());
        assertEquals("2007-06-29 00:00:00.0", analysisDTORec.getLastUpdateRa().toString());
        assertEquals("2", analysisDTORec.getTotalLi().toString());
        assertEquals("2017-12-21 00:00:00.0", analysisDTORec.getLastUpdateLi().toString());
        assertEquals("0", analysisDTORec.getEeaCore().toString());
        assertEquals("0", analysisDTORec.getEeaPriority().toString());
        assertEquals("0", analysisDTORec.getFlaggedRa().toString());
        assertEquals("0", analysisDTORec.getNoIssue().toString());

    }

}
