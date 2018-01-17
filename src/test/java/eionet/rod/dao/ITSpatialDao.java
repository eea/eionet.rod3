package eionet.rod.dao;

import org.junit.runner.RunWith;

import org.junit.Test;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import eionet.rod.model.ObligationCountry;
import eionet.rod.model.Spatial;

import eionet.rod.util.exception.ResourceNotFoundException;


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
	private SpatialDao spatialDao;
	
		
    @Test
    public void testfinId() throws ResourceNotFoundException
    {
    	Spatial spatial = spatialDao.findId(1);
    	System.out.println("Id: " + spatial.getSpatialId());
    }

    
	
    @Test
    public void testfinAll() throws ResourceNotFoundException
    {
    	List<Spatial> allMembers = spatialDao.findAll();
    	System.out.println("Id All: " + allMembers.size());

    }

    @Test
    public void testfinAllMember() throws ResourceNotFoundException
    {
    	List<Spatial> allMember = spatialDao.findAllMember("E");
    	System.out.println("Id All Member: " + allMember.size());
    	List<Spatial> allNoMember = spatialDao.findAllMember("N");
    	System.out.println("Id All No Member: " + allNoMember.size());
    }

    @Test
    public void testfindObligationCountriesList() throws ResourceNotFoundException
    {
    	List<ObligationCountry> obligationCountries = spatialDao.findObligationCountriesList(446);
    	for (int i = 0 ; i< obligationCountries.size(); i++) {
    		System.out.println("issue: " + obligationCountries.get(i).getCountryName());
    	}
    }
}
