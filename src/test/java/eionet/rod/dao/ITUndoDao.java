package eionet.rod.dao;

import eionet.rod.model.Obligations;
import eionet.rod.model.UndoDTO;
import eionet.rod.service.ObligationService;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.util.List;

import static org.junit.Assert.*;


/**
 * Test the spatial dao.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})
@Sql("/seed-undo.sql")
public class ITUndoDao {

    @Autowired
    private UndoDao undoDao;

    @Autowired
    private ObligationService obligationsService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testInsertIntoUndo1() {

        long ts = System.currentTimeMillis();

        undoDao.insertIntoUndo(ts, "T_SOURCE", "ALIAS", "U", "y", "n", "Test Alias", 0, "y");
        undoDao.insertIntoUndo(ts, "T_SOURCE", "PK_SOURCE_ID", "U", "n", "y", "1", 0, "y");

        List<UndoDTO> undoList = undoDao.getUndoList(ts, "T_SOURCE", "U");

        assertEquals(undoList.get(0).getCol(), "ALIAS");
        assertEquals(undoList.get(1).getValue(), "1");

    }

    @Test
    public void testInsertIntoUndo2() {

        long ts = System.currentTimeMillis();

        undoDao.insertIntoUndo(1, "U", "T_OBLIGATION", "PK_RA_ID", ts, "", "y");

        List<UndoDTO> undoList = undoDao.getUndoList(ts, "T_OBLIGATION", "U");
        Obligations obligation = obligationsService.findOblId(1);

        for (UndoDTO undoDTO : undoList) {
            if ("TITLE".equals(undoDTO.getCol())) {
                assertEquals(undoDTO.getValue(), obligation.getOblTitle());
            } else if ("COMMENT".equals(undoDTO.getCol())) {
                assertEquals(undoDTO.getValue(), obligation.getComment());
            }
        }

    }

    @Test
    public void testInsertTransactionInfo() {

        long ts = System.currentTimeMillis();

        undoDao.insertTransactionInfo(1, "A", "T_OBLIGATION", "PK_RA_ID", ts, "");
        undoDao.insertTransactionInfo(1, "A", "T_RASPATIAL_LNK", "FK_RA_ID", ts, "");

        List<UndoDTO> undoObligationsList = undoDao.getUndoList(ts, "T_OBLIGATION", "A");
        List<UndoDTO> undoSpatialLnkList = undoDao.getUndoList(ts, "T_RASPATIAL_LNK", "A");

        assertEquals(undoObligationsList.get(0).getValue(), "PK_RA_ID = 1 ");
        assertEquals(undoSpatialLnkList.get(0).getValue(), "FK_RA_ID = 1 ");

    }

    @Test
    public void testGetPreviousActionsReportSpecific() {

        List<UndoDTO> undoList = undoDao.getPreviousActionsReportSpecific(1, "T_OBLIGATION", "PK_RA_ID", "U");
        assertEquals(undoList.get(0).getValue(), "arroyyol");

        List<UndoDTO> undoList2 = undoDao.getPreviousActionsReportSpecific(9, "T_OBLIGATION", "PK_RA_ID", "U");
        assertEquals(0, undoList2.size());

    }

    @Test
    public void testGetUndoList() {

        String ts = "1519895729320";

        List<UndoDTO> undoList = undoDao.getUndoList(Long.parseLong(ts), "T_OBLIGATION", "U");

        for (UndoDTO undoDTO : undoList) {
            if ("COORDINATOR".equals(undoDTO.getCol())) {
                assertEquals(undoDTO.getValue(), "Country contacts");
            } else if ("DATA_USED_FOR".equals(undoDTO.getCol())) {
                assertEquals(undoDTO.getValue(), "http://www.pops.int/documents/implementation/nips/submissions/default.htm");
            }
        }

    }

    @Test
    public void testGetPreviousActionsGeneral() {

        List<UndoDTO> undoList = undoDao.getPreviousActionsGeneral();

        assertEquals(undoList.get(0).getUserName(), "arroyyol");
        assertEquals(undoList.get(0).getTab(), "T_OBLIGATION");
        assertEquals(undoList.get(0).getOperation(), "U");

    }

    @Test
    public void testIsDelete() {

        boolean isDelete = undoDao.isDelete("T_SOURCE", "PK_SOURCE_ID", 1);
        assertTrue(isDelete);

        isDelete = undoDao.isDelete("T_OBLIGATION", "PK_RA_ID", 1);
        assertFalse(isDelete);

    }

    @Test
    public void testAddObligationIdsIntoUndo() {

        String ts = "1520339150385";

        undoDao.addObligationIdsIntoUndo(1, Long.parseLong(ts), "T_SOURCE");

        List<UndoDTO> undoList = undoDao.getUndoList(Long.parseLong(ts), "T_SOURCE", "O");
        assertEquals(undoList.get(0).getValue(), "1,2");

    }

    @Test
    public void testGetUpdateHistory() {

        List<UndoDTO> history = undoDao.getUpdateHistory("", new MapSqlParameterSource());
        assertEquals(history.get(0).getDescription(), "Fuel Quality Directive Article 7a");
        assertEquals(history.get(0).getOperation(), "U");

    }

    @Test
    public void testGetUpdateHistory_with_ExtraSql_Value() {
        String username = "arroyyol";
        String extraSQL = "AND U2.VALUE = :extraSQL ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("extraSQL", username);

        List<UndoDTO> history = undoDao.getUpdateHistory(extraSQL, params);
        assertEquals(history.size(), 1);
        assertEquals(history.get(0).getDescription(), "Fuel Quality Directive Article 7a");
        assertEquals(history.get(0).getOperation(), "U");
    }

    @Test
    public void testGetUpdateHistory_with_Sql_Injection() {
        String username_sqlInjection = "%27%20UNION%20ALL%20SELECT%20CONCAT(0x2228,database(),0x2922),NULL,NULL,NULL,NULL,NULL--%20-";
        String extraSQL = "AND U2.VALUE = :extraSQL ";
        MapSqlParameterSource params = new MapSqlParameterSource();
        params.addValue("extraSQL", username_sqlInjection);

        List<UndoDTO> history = undoDao.getUpdateHistory(extraSQL, params);
        assertEquals(history.size(), 0);
    }

    @Test
    public void testGetUndoInformation() {

        String ts = "1519895729320";

        List<UndoDTO> undoList = undoDao.getUndoInformation(Long.parseLong(ts), "U", "T_OBLIGATION");

        for (UndoDTO undoDTO : undoList) {
            if ("DATE_COMMENTS".equals(undoDTO.getCol())) {
                assertEquals(undoDTO.getValue(), "For Member States that acceeded to the EU on 1 January 2007 (Bulgaria, Romania) the reporting concerning the period 2004-2006 is voluntary.");
            } else if ("FIRST_REPORTING".equals(undoDTO.getCol())) {
                assertEquals(undoDTO.getValue(), "2005-10-31");
            }
        }

    }

}
