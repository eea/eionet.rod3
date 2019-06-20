package eionet.rod.dao;


import eionet.rod.model.ClientDTO;
import eionet.rod.model.InstrumentDTO;
import eionet.rod.model.Obligations;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * Test the client operations.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})

@Sql("/seed-obligation-source.sql")
public class ClientServiceIT {

    @Autowired
    private WebApplicationContext ctx;

    @Autowired
    private ClientService clientService;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void productionTest() throws Exception {
        ClientService clientService = ctx.getBean("clientService", ClientService.class);
        insertAndDelete(clientService);

        getDirectIndirectObligations(clientService);

        getDirectIndirectInstruments(clientService);

        insertSelectbyIDs(clientService);

    }

    private void insertAndDelete(ClientService clientService) throws Exception {
        ClientDTO newRec = new ClientDTO();

        newRec.setName("CLIENT 1");
        newRec.setAcronym("TLA");
        clientService.insert(newRec);

        List<ClientDTO> allClients;
        allClients = clientService.getAllClients();

        // System.out.print("size: " + allClients.size());
        assertEquals(3, allClients.size());

        Integer newId = allClients.get(0).getClientId();
        ClientDTO upload = clientService.getById(newId);

        assertEquals(newRec.getName(), upload.getName());

        // System.out.print("name: " + upload.getName());
        clientService.deleteById(newId);

        allClients = clientService.getAllClients();

        // System.out.print("size: " + allClients.size());
        assertEquals(2, allClients.size());

        //exception.expect(IOException.class);
        // Can't delete twice.
        //clientService.deleteById(newId);
    }

    private void insertSelectbyIDs(ClientService clientService) {
        String clientsIds = "1,2,";
        clientService.deleteByIds(clientsIds);
    }

    private void getDirectIndirectObligations(ClientService clientService) {
        List<Obligations> directObligations = clientService.getDirectObligations(1);
        assertEquals("1", directObligations.get(0).getObligationId().toString());
        assertEquals(1, directObligations.size());

        List<Obligations> indirectObligations = clientService.getIndirectObligations(1);
        assertEquals(2, indirectObligations.size());
        assertEquals("1", indirectObligations.get(0).getObligationId().toString());
        assertEquals("2", indirectObligations.get(1).getObligationId().toString());
    }


    private void getDirectIndirectInstruments(ClientService clientService) {
        List<InstrumentDTO> directInstruments = clientService.getDirectInstruments(1);
        //System.out.print(directInstruments.size());
        assertEquals("1", directInstruments.get(0).getSourceId().toString());
        assertEquals(1, directInstruments.size());

        List<InstrumentDTO> indirectInstruments = clientService.getIndirectInstruments(2);
        assertEquals(1, indirectInstruments.size());
        assertEquals("2", indirectInstruments.get(0).getSourceId().toString());

    }

    @Test
    public void testGetOrganisationNameByID() {
        String organisationName = clientService.getOrganisationNameByID("1");
        assertEquals("TC-Test client", organisationName);
    }

}
