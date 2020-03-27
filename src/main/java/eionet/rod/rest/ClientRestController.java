package eionet.rod.rest;

import eionet.rod.model.ClientDTO;
import eionet.rod.service.ClientService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Client rest controller.
 */
@RestController
@RequestMapping("/rest/client")
public class ClientRestController {

  @Autowired
  private ClientService clientService;

  /**
   * Find clients list.
   *
   * @return the list
   */
  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public List<ClientDTO> findAll(){
    return clientService.getAllClients();
  }
}
