package eionet.rod.dao;

import java.io.InputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.Test;
import org.junit.Rule;
import org.junit.rules.ExpectedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.context.WebApplicationContext;

import eionet.rod.model.ClientDTO;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(locations = {"classpath:spring-mvc-config.xml",
        "classpath:spring-db-config.xml"})

/**
 * Test the client operations.
 */
public class ClientServiceIT {

    @Autowired
    private WebApplicationContext ctx;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @Test
    public void productionTest() throws Exception {
        ClientService clientService = ctx.getBean("clientService", ClientService.class);
        //insertAndDelete(clientService);
    }

    private void insertAndDelete(ClientService clientService) throws Exception {
        ClientDTO newRec = new ClientDTO();

        newRec.setName("CLIENT 1");
        newRec.setAcronym("TLA");
        clientService.insert(newRec);

        List<ClientDTO> allClients;
        allClients = clientService.getAllClients();
        assertEquals(1, allClients.size());

        Integer newId = allClients.get(0).getClientId();
        ClientDTO upload = clientService.getById(newId);

        clientService.deleteById(newId);

        exception.expect(IOException.class);
        // Can't delete twice.
        clientService.deleteById(newId);
    }
}
