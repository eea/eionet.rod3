package eionet.rod.dao;

import eionet.rod.model.*;
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

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;


@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml",
        "classpath:spring-security.xml"})
@Sql("/seed-obligation-source.sql")

public class ITSourceDao {

    @Autowired
    private SourceDao sourceDao;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void testfgetById() {
        InstrumentFactsheetDTO intrument = sourceDao.getById(1);
        assertEquals("22222", intrument.getSourceCode());
        assertEquals("Basel Convention", intrument.getSourceAlias());

        exception.expect(ResourceNotFoundException.class);
        intrument = sourceDao.getById(12);

        // todo implement
    }

    @Test
    public void getObligationsById() {
        List<InstrumentObligationDTO> instruments = sourceDao.getObligationsById(1);

        assertEquals(2, instruments.size());
        assertEquals("1", instruments.get(0).getObligationId().toString());
        assertEquals("Fuel Quality Directive Article 7a", instruments.get(0).getTitle());

    }


    @Test
    public void testinsertUpdateDelete() throws ResourceNotFoundException {

        InstrumentFactsheetDTO instrumentFactsheetRec = new InstrumentFactsheetDTO();
        instrumentFactsheetRec.setSourceTitle("Test title");
        instrumentFactsheetRec.setSourceAlias("Test alias");
        instrumentFactsheetRec.setSourceCode("222222");
        instrumentFactsheetRec.setSourceTerminate("N");
        instrumentFactsheetRec.setSourceUrl("https://www.google.es");
        instrumentFactsheetRec.setSourceCelexRef("31977D0795");
        instrumentFactsheetRec.setClientId(1);
        instrumentFactsheetRec.setSourceLnkFKSourceParentId(1);
        instrumentFactsheetRec.setSourceValidFrom(null);
        instrumentFactsheetRec.setSourceEcEntryIntoForce(null);
        instrumentFactsheetRec.setSourceEcAccession(null);
        instrumentFactsheetRec.setSourceFkTypeId(15);

        List<String> selectedClassifications = new ArrayList<>();

        selectedClassifications.add("1");

        instrumentFactsheetRec.setSelectedClassifications(selectedClassifications);
        Integer sourceId = sourceDao.insert(instrumentFactsheetRec);

        InstrumentFactsheetDTO instrumentsResult = sourceDao.getById(sourceId);
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
        instrumentFactsheetRec.setSourceValidFrom(null);
        instrumentFactsheetRec.setSourceEcEntryIntoForce(null);
        instrumentFactsheetRec.setSourceEcAccession(null);

        sourceDao.update(instrumentFactsheetRec);

        sourceDao.delete(sourceId);

        exception.expect(ResourceNotFoundException.class);
        InstrumentFactsheetDTO intrument = sourceDao.getById(sourceId);

    }

    @Test
    public void testgetAllInstruments() {
        List<InstrumentFactsheetDTO> instruments = sourceDao.getAllInstruments();
        assertEquals(2, instruments.size());
        assertEquals("1", instruments.get(0).getSourceId().toString());
        assertEquals("Basel Convention", instruments.get(0).getSourceAlias());

    }

    @Test
    public void testgetAllClassifications() {

        List<InstrumentClassificationDTO> allClassifications = sourceDao.getAllClassifications();
        assertEquals(2, allClassifications.size());
        assertEquals("1", allClassifications.get(0).getClassId().toString());
        assertEquals("Legal Instrument", allClassifications.get(0).getClassName());

    }

    @Test
    public void testinsertClassificationsandDelete() {
        InstrumentFactsheetDTO instrumentFactsheetRec = new InstrumentFactsheetDTO();
        List<String> selectedclass = new ArrayList<>();
        selectedclass.add("2");
        instrumentFactsheetRec.setSelectedClassifications(selectedclass);
        instrumentFactsheetRec.setSourceId(1);

        sourceDao.insertClassifications(instrumentFactsheetRec);

        sourceDao.deleteClassifications(instrumentFactsheetRec.getSourceId());

    }

    @Test
    public void testgetHierarchy() {
        String result = sourceDao.getHierarchy(1, false, "X");
        assertEquals(0, result.length());
        result = sourceDao.getHierarchy(2, false, "X");
        assertNotNull(result);
    }


    @Test
    public void testgetHierarchyInstrument() {
        InstrumentsListDTO result = sourceDao.getHierarchyInstrument(1);
        assertEquals("2", result.getParentId().toString());
    }

    @Test
    public void testgetHierarchyInstruments() {
        List<HierarchyInstrumentDTO> intruments = sourceDao.getHierarchyInstruments(1);
        assertNotEquals(0, intruments.size());
    }
}
