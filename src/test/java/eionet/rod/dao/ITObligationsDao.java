package eionet.rod.dao;

import org.junit.runner.RunWith;
import org.junit.Rule;
//import org.junit.Rule;
import org.junit.Test;
//import org.junit.rules.ExpectedException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import org.junit.rules.ExpectedException;

//import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.SiblingObligation;
import eionet.rod.model.Spatial;
import eionet.rod.service.ObligationService;
import eionet.rod.util.exception.ResourceNotFoundException;


/**
 * Test the spatial dao.
 */


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

public class ITObligationsDao {
    
//	@Rule
//	public ExpectedException exception = ExpectedException.none();
	
	@Autowired
	private ObligationService obligationsService;
	
    @Rule
    public ExpectedException exception = ExpectedException.none();
	
		
    @Test
    public void testfindAll() throws ResourceNotFoundException
    {
    	List<Obligations> obligations = obligationsService.findAll();
    	assertEquals("1",obligations.get(0).getObligationId().toString());
    	assertEquals("Fuel Quality Directive Article 7a",obligations.get(0).getOblTitle());
    	assertEquals("2",obligations.get(1).getObligationId().toString());
    	assertEquals("Test 2 - Fuel Quality Directive Article 7a",obligations.get(1).getOblTitle());
   	
    }
    
    @Test
    public void testfindObligationList () throws ResourceNotFoundException
    {
    	List<Obligations> obligations = obligationsService.findObligationList("0","0","0","N","0");
    	System.out.print(obligations.size());
    	assertEquals("1",obligations.get(0).getObligationId().toString());
    	assertEquals("eionet-nrc-waterquality", obligations.get(0).getRespRoleId());
    	assertEquals("Test client", obligations.get(0).getClientName());
    	
    	obligations = obligationsService.findObligationList("0","0","0","Y","0");
    	assertEquals("2",obligations.get(0).getObligationId().toString());
    	assertEquals("eionet-nrc-waterquality", obligations.get(0).getRespRoleId());
    	assertNull(obligations.get(0).getClientName());
    }
    
    @Test
    public void testfindOblId()
    {
    	Obligations obligation = obligationsService.findOblId(1);
    	assertEquals("1",obligation.getObligationId().toString());
    	assertEquals("Fuel Quality Directive Article 7a",obligation.getOblTitle());
    	
    	exception.expect(ResourceNotFoundException.class);	
    	obligation = obligationsService.findOblId(12);
    }
    
    @Test
    public void testfindSiblingObligations() throws ResourceNotFoundException
    {
    	List<SiblingObligation> siblingObligations = obligationsService.findSiblingObligations(1);
    	System.out.print("size:" + siblingObligations.size());
    	System.out.print("obl:" + siblingObligations.get(0).getSiblingOblId());
    	System.out.print("fk_source:" + siblingObligations.get(0).getFkSourceId());
    	assertEquals("2",siblingObligations.get(0).getSiblingOblId());
    	assertEquals("1",siblingObligations.get(0).getFkSourceId());
    	assertEquals("Test 2 - Fuel Quality Directive Article 7a",siblingObligations.get(0).getSiblingTitle());
    	assertEquals("Y",siblingObligations.get(0).getTerminate());
    	assertEquals("Article 15",siblingObligations.get(0).getAuthority());
    	List<SiblingObligation> siblingObligationsNull = obligationsService.findSiblingObligations(12);
    	assertNull("Null", siblingObligationsNull);
    }
    
    @Test 
    public void findAllCountriesByObligation() 
    {
    	List<Spatial> spatialNoVol = obligationsService.findAllCountriesByObligation(1,"N");
    	System.out.print(spatialNoVol.size());
    	
    	assertEquals("1",spatialNoVol.get(0).getSpatialId().toString());
    	assertEquals("Austria",spatialNoVol.get(0).getName());
    	assertNull(obligationsService.findAllCountriesByObligation(1,"C"));
    	
    
    }
    
    @Test
    public void testfindAllIssuesbyObligation()
    {
    	List<Issue> issues = obligationsService.findAllIssuesbyObligation(1);
    	assertEquals("1",issues.get(0).getIssueId().toString());
    	assertEquals("Climate Change", issues.get(0).getIssueName());
    	
    	assertNull(obligationsService.findAllIssuesbyObligation(12));
    }
    
    @Test
    public void testinsertupdateObligation() throws ResourceNotFoundException
    {
    	//data obligations
    	Obligations obligation = new Obligations();
    	obligation.setOblTitle("Test insert OBLIGATION");
    	obligation.setSourceId("1");
    	obligation.setDescription("Test DEscription Obligation");
    	obligation.setTerminate("N");
    	obligation.setCoordinatorRoleSuf("1");
    	obligation.setResponsibleRoleSuf("0");
    	obligation.setValidTo("21/12/2017");
    	obligation.setClientId("1");
    	obligation.setReportFreqMonths("5");
    	
    	//data ClientDTO
    	List<ClientDTO> clients = new ArrayList<ClientDTO>();
    	ClientDTO client = new ClientDTO();
    	client.setClientId(1);
    	clients.add(client);
    	
    	List<Spatial> spatials = new ArrayList<Spatial>();
    	Spatial spatial = new Spatial();
    	spatial.setSpatialId(1);
    	spatials.add(spatial);
    	spatial = new Spatial();
    	spatial.setSpatialId(2);
    	spatials.add(spatial);
    	
    	List<Spatial> spatialsVoluntary = new ArrayList<Spatial>();
    	Spatial spatialVoluntary = new Spatial();
    	spatialVoluntary.setSpatialId(3);
    	spatialsVoluntary.add(spatialVoluntary);
    	
    	
    	List<Issue> issues = new ArrayList<Issue>();
    	Issue issue = new Issue();
    	issue.setIssueId(1);
    	issues.add(issue);

    	
    	Integer intObligationId = obligationsService.insertObligation(obligation,clients,spatials, spatialsVoluntary, issues);
    	
    	Obligations obliagationResult = obligationsService.findOblId(intObligationId);
    	assertEquals(obligation.getOblTitle(), obliagationResult.getOblTitle());
    	assertEquals(obligation.getClientId(), obliagationResult.getClientId());
    	
    	obligation.setObligationId(intObligationId);
    	obligation.setDescription("Update Test Dscription Obligation");
    	obligation.setReportFreqMonths("10");
    	
    	spatials = new ArrayList<Spatial>();
    	spatial = new Spatial();
    	spatial.setSpatialId(1);
    	spatials.add(spatial);
    	
    	spatialsVoluntary = new ArrayList<Spatial>();
    	spatialVoluntary = new Spatial();
    	spatialVoluntary.setSpatialId(2);
    	spatialsVoluntary.add(spatialVoluntary);
    	
    	issues = new ArrayList<Issue>();
    	issue = new Issue();
    	issue.setIssueId(2);
    	issues.add(issue);
	    	
    	obligationsService.updateObligations(obligation, null, spatials, spatialsVoluntary, issues);
    	
    	obligationsService.deleteObligations(intObligationId.toString());
    	
//    	try {
    	exception.expect(ResourceNotFoundException.class);	
    	obligation = obligationsService.findOblId(intObligationId);
  	        
//    	}
//    	catch(ResourceNotFoundException re)
//    	{
//    	      String message = "The obligation you requested with id " + intObligationId + " was not found in the database";
//    	      assertEquals(message, re.getMessage());
//
//    	      throw re;
//    	}
    	    	    	

   }
    
}
