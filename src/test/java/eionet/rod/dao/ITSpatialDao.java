package eionet.rod.dao;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;
import eionet.rod.service.SpatialService;
import eionet.rod.util.exception.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;


/**
 * Test the spatial dao.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

public class ITSpatialDao {

    @Autowired
    private SpatialService spatialService;


    @Test
    public void testfinId() {
        Spatial spatial = spatialService.findOne(1);
        assertEquals("1", spatial.getSpatialId().toString());
        assertEquals("Austria", spatial.getName());

    }


    @Test
    public void testfinAll() throws ResourceNotFoundException {
        List<Spatial> allMembers = spatialService.findAll();
        assertEquals(3, allMembers.size());
        assertEquals("2", allMembers.get(0).getSpatialId().toString());
        assertEquals("Albania", allMembers.get(0).getName());
        assertEquals("1", allMembers.get(1).getSpatialId().toString());
        assertEquals("Austria", allMembers.get(1).getName());
        assertEquals("3", allMembers.get(2).getSpatialId().toString());
        assertEquals("Francia", allMembers.get(2).getName());
    }

    @Test
    public void testfinAllMember() throws ResourceNotFoundException {
        List<Spatial> allMember = spatialService.findAllMember("Y");
        assertEquals(2, allMember.size());
        List<Spatial> allNoMember = spatialService.findAllMember("N");
        assertEquals(1, allNoMember.size());
        List<Spatial> allMemberNull = spatialService.findAllMember("E");
        assertEquals(0, allMemberNull.size());
    }

    @Test
    public void testfindObligationCountriesList() throws ResourceNotFoundException {
        List<Spatial> spatialListY = new ArrayList<>();
        spatialListY.add(new Spatial(1, "Austria", "C", "AT", "Y"));
        spatialListY.add(new Spatial(2, "Albania", "C", "AL", "N"));
        spatialListY.add(new Spatial(3, "Francia", "C", "FR", "Y"));

        List<ObligationCountry> obligationCountries = spatialService.findObligationCountriesList(1);
        for (int i = 0; i < obligationCountries.size(); i++) {
            //System.out.println("country: " + obligationCountries.get(i).getCountryName());
            assertEquals(spatialListY.get(i).getName(), obligationCountries.get(i).getCountryName());
        }

    }

}
