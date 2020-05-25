package eionet.rod.rest;

import eionet.rod.model.ClientDTO;
import eionet.rod.service.ClientService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class ClientRestControllerTest {

  @InjectMocks
  private ClientRestController clientRestController;

  @Mock
  private ClientService clientService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void findAll() {
    List<ClientDTO> clients = new ArrayList<>();
    ClientDTO client = new ClientDTO();
    client.setClientId(1);
    clients.add(client);
    Mockito.when(clientService.getAllClients()).thenReturn(clients);
    List<ClientDTO> result = clientRestController.findAll();
    Assert.assertNotNull(result);
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).getClientId().intValue(), 1);
  }
}