package eionet.rod.dao;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import eionet.rod.model.InstrumentClassificationDTO;
import eionet.rod.model.InstrumentFactsheetDTO;
import eionet.rod.model.InstrumentObligationDTO;
import eionet.rod.util.exception.ResourceNotFoundException;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})

public class ITSourceService {

	@Autowired
	private SourceService sourceService;
	
    @Rule
    public ExpectedException exception = ExpectedException.none();
	
    @Test
    public void testfgetById() 
    {
    	InstrumentFactsheetDTO intrument = sourceService.getById(1);
    	assertEquals("22222",intrument.getSourceCode());
    	assertEquals("Basel Convention",intrument.getSourceAlias());
    	
    	exception.expect(ResourceNotFoundException.class);	
    	intrument = sourceService.getById(12);
    }
    
    @Test 
    public void getObligationsById()
    {
    	List<InstrumentObligationDTO> instruments = sourceService.getObligationsById(1);
    	
    	assertEquals(2,instruments.size());
    	assertEquals("1",instruments.get(0).getObligationId().toString());
    	assertEquals("Fuel Quality Directive Article 7a", instruments.get(0).getTitle());
    	
    }
    
    
    @Test
    public void testinsertUpdateDelete()  throws ResourceNotFoundException {
    	
		InstrumentFactsheetDTO instrumentFactsheetRec = new InstrumentFactsheetDTO();
    	instrumentFactsheetRec.setSourceTitle("Test title");
    	instrumentFactsheetRec.setSourceAlias("Test alias");
    	instrumentFactsheetRec.setSourceCode("222222");
    	instrumentFactsheetRec.setSourceTerminate("N");
    	instrumentFactsheetRec.setSourceUrl("https://www.google.es");
    	instrumentFactsheetRec.setSourceCelexRef("31977D0795");
    	instrumentFactsheetRec.setClientId(1);
    	instrumentFactsheetRec.setSourceLnkFKSourceParentId(1);
    	instrumentFactsheetRec.setSourceValidFrom("");
    	instrumentFactsheetRec.setSourceEcEntryIntoForce("");
    	instrumentFactsheetRec.setSourceEcAccession("");
    	
    	List<String> selectedClassifications =new ArrayList<String>();
    	
    	selectedClassifications.add("1");
    	
    	instrumentFactsheetRec.setSelectedClassifications(selectedClassifications);
    	Integer sourceId = sourceService.insert(instrumentFactsheetRec);
    	
    	InstrumentFactsheetDTO instrumentsResult = sourceService.getById(sourceId);
    	assertEquals(instrumentFactsheetRec.getSourceTitle(), instrumentsResult.getSourceTitle());
    	assertEquals(instrumentFactsheetRec.getClientId(), instrumentsResult.getClientId());
    	    	
	    instrumentFactsheetRec.setSourceTitle("Test title2");
		instrumentFactsheetRec.setSourceAlias("Test alias2");
		instrumentFactsheetRec.setSourceCode("222322");
		instrumentFactsheetRec.setSourceTerminate("N");
    	instrumentFactsheetRec.setSourceUrl("https://www.google.es");
    	instrumentFactsheetRec.setSourceCelexRef("31977D0795");
    	instrumentFactsheetRec.setClientId(1);
    	instrumentFactsheetRec.setSourceLnkFKSourceParentId(1);
    	instrumentFactsheetRec.setSourceValidFrom("");
    	instrumentFactsheetRec.setSourceEcEntryIntoForce("");
    	instrumentFactsheetRec.setSourceEcAccession("");
		
		sourceService.update(instrumentFactsheetRec);
		
		sourceService.delete(sourceId);
    		    			    			
		exception.expect(ResourceNotFoundException.class);	
		InstrumentFactsheetDTO intrument = sourceService.getById(sourceId);
		
    }
    
    @Test 
    public void testgetAllInstruments() {
    	List<InstrumentFactsheetDTO> instruments = sourceService.getAllInstruments();
    	assertEquals(2,instruments.size());
    	assertEquals("1",instruments.get(0).getSourceId().toString());
    	assertEquals("Basel Convention", instruments.get(0).getSourceAlias());

    }
    
    @Test
    public void testgetAllClassifications() {
    
    	List<InstrumentClassificationDTO> AllClassifications = sourceService.getAllClassifications();
    	assertEquals(2,AllClassifications.size());
    	assertEquals("1",AllClassifications.get(0).getClassId().toString());
    	assertEquals("Legal Instrument", AllClassifications.get(0).getClassName());
    
    }
    
    @Test 
    public void testinsertClassificationsandDelete() {
    	InstrumentFactsheetDTO instrumentFactsheetRec = new InstrumentFactsheetDTO();
    	List<String> selectedclass =  new ArrayList<String>();
    	selectedclass.add("2");
    	instrumentFactsheetRec.setSelectedClassifications(selectedclass);
    	instrumentFactsheetRec.setSourceId(1);
    	
    	sourceService.insertClassifications(instrumentFactsheetRec);
    	
    	sourceService.deleteClassifications(instrumentFactsheetRec.getSourceId());
    	
    }
    @Test 
    public void testgetHierarchy() {
    	String result = sourceService.getHierarchy(1, false, "X");
    	System.out.print("result" + result);
    }
    
    
}
