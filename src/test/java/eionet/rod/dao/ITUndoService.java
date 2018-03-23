package eionet.rod.dao;

import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import eionet.rod.model.Obligations;
import eionet.rod.model.UndoDTO;
import eionet.rod.service.ObligationService;



/**
 * Test the spatial dao.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})
@Sql("/seed-undo.sql")
public class ITUndoService {
	
	@Autowired
    private UndoService undoService;
	
	@Autowired
    private ObligationService obligationsService;
    
    @Rule
    public ExpectedException exception = ExpectedException.none();
     
    @Test
    public void testInsertIntoUndo1() {
    	
    	long ts = System.currentTimeMillis();
    	
    	undoService.insertIntoUndo(ts, "T_SOURCE", "ALIAS", "U", "y", "n", "Test Alias", 0, "y");
    	undoService.insertIntoUndo(ts, "T_SOURCE", "PK_SOURCE_ID", "U", "n", "y", "1", 0, "y");    	
    	
    	List<UndoDTO> undoList = undoService.getUndoList(ts, "T_SOURCE", "U");
    	
    	assertEquals(undoList.get(0).getCol(), "ALIAS");
    	assertEquals(undoList.get(1).getValue(), "1");
    	
    }
    
    @Test
    public void testInsertIntoUndo2() {
    	
    	long ts = System.currentTimeMillis();
    	
    	undoService.insertIntoUndo(1, "U", "T_OBLIGATION", "PK_RA_ID", ts, "", "y");
    	
    	List<UndoDTO> undoList = undoService.getUndoList(ts, "T_OBLIGATION", "U");
    	Obligations obligation = obligationsService.findOblId(1);
    	
    	for (int i = 0; i < undoList.size(); i++) {
    		if (undoList.get(i).getCol().equals("TITLE")) {
    			assertEquals(undoList.get(i).getValue(), obligation.getOblTitle());
    		} else if (undoList.get(i).getCol().equals("COMMENT")) {
    			assertEquals(undoList.get(i).getValue(), obligation.getComment());
    		}
    	}
    	
    }
    
    @Test
    public void testInsertTransactionInfo() {
    	
    	long ts = System.currentTimeMillis();
    	
    	undoService.insertTransactionInfo(1, "A", "T_OBLIGATION", "PK_RA_ID", ts, "");
    	undoService.insertTransactionInfo(1, "A", "T_RASPATIAL_LNK", "FK_RA_ID", ts, "");
    	
    	List<UndoDTO> undoObligationsList = undoService.getUndoList(ts, "T_OBLIGATION", "A");
    	List<UndoDTO> undoSpatialLnkList = undoService.getUndoList(ts, "T_RASPATIAL_LNK", "A");
    	
    	assertEquals(undoObligationsList.get(0).getValue(), "PK_RA_ID = 1 ");
    	assertEquals(undoSpatialLnkList.get(0).getValue(), "FK_RA_ID = 1 ");
    	
    }
    
    @Test
    public void testGetPreviousActionsReportSpecific() {
    	
    	List<UndoDTO> undoList = undoService.getPreviousActionsReportSpecific(1, "T_OBLIGATION", "PK_RA_ID", "U");
    	assertEquals(undoList.get(0).getValue(), "arroyyol");
    	
    	List<UndoDTO> undoList2 = undoService.getPreviousActionsReportSpecific(9, "T_OBLIGATION", "PK_RA_ID", "U");
    	assertNull(undoList2);
    	
    }
    
    @Test
    public void testGetUndoList() {    	
    	
    	String ts = "1519895729320";
    	    	    	
    	List<UndoDTO> undoList = undoService.getUndoList(Long.parseLong(ts), "T_OBLIGATION", "U");
    	
    	for (int i = 0; i < undoList.size(); i++) {
    		if (undoList.get(i).getCol().equals("COORDINATOR")) {
    			assertEquals(undoList.get(i).getValue(), "Country contacts");
    		} else if (undoList.get(i).getCol().equals("DATA_USED_FOR")) {
    			assertEquals(undoList.get(i).getValue(), "http://www.pops.int/documents/implementation/nips/submissions/default.htm");
    		}
    	}
    	
    }
    
    @Test
    public void testGetPreviousActionsGeneral() {
    	
    	List<UndoDTO> undoList = undoService.getPreviousActionsGeneral();
    	
    	assertEquals(undoList.get(0).getUserName(), "arroyyol");
    	assertEquals(undoList.get(0).getTab(), "T_OBLIGATION");
    	assertEquals(undoList.get(0).getOperation(), "U");
    	
    }
    
    @Test
    public void testIsDelete() {
    	
    	boolean isDelete = undoService.isDelete("T_SOURCE", "PK_SOURCE_ID", 1);
    	assertTrue(isDelete);
    	
    	isDelete = undoService.isDelete("T_OBLIGATION", "PK_RA_ID", 1);
    	assertFalse(isDelete);
    	
    }
    
    @Test
    public void testAddObligationIdsIntoUndo() {
    	
    	String ts = "1520339150385";
    	
    	undoService.addObligationIdsIntoUndo(1, Long.parseLong(ts), "T_SOURCE");
    	
    	List<UndoDTO> undoList = undoService.getUndoList(Long.parseLong(ts), "T_SOURCE", "O");
    	assertEquals(undoList.get(0).getValue(), "1,2");
    	
    }
    
    @Test
    public void testGetUpdateHistory() {
    	
    	List<UndoDTO> history = undoService.getUpdateHistory("");
    	assertEquals(history.get(0).getDescription(), "Fuel Quality Directive Article 7a");
    	assertEquals(history.get(0).getOperation(), "U");
    	
    }
    
    @Test
    public void testGetUndoInformation() {
    	
    	String ts = "1519895729320";
    	
    	List<UndoDTO> undoList = undoService.getUndoInformation(Long.parseLong(ts), "U", "T_OBLIGATION");
    	
    	for (int i = 0; i < undoList.size(); i++) {
    		if (undoList.get(i).getCol().equals("DATE_COMMENTS")) {
    			assertEquals(undoList.get(i).getValue(), "For Member States that acceeded to the EU on 1 January 2007 (Bulgaria, Romania) the reporting concerning the period 2004-2006 is voluntary.");
    		} else if (undoList.get(i).getCol().equals("FIRST_REPORTING")) {
    			assertEquals(undoList.get(i).getValue(), "2005-10-31");
    		}
    	}
    	
    }
       
}
