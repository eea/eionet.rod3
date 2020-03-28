package eionet.rod.rest;

import eionet.rod.model.Obligations;
import eionet.rod.service.ObligationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Obligation rest controller.
 */
@RestController
@RequestMapping("/rest/obligation")
public class ObligationRestController {

  @Autowired
  private ObligationService obligationService;

  /**
   * Find opened obligations list.
   *
   * @return the list
   */
  @RequestMapping(value = "/findOpened", method = RequestMethod.GET)
  public List<Obligations> findOpenedObligations(){
    return obligationService.findObligationList(null,null,null,"N",null,null,null,null,false);
  }

  /**
   * Find opened obligations obligations.
   *
   * @param obligationId the id
   *
   * @return the obligations
   */
  @RequestMapping(value = "/{obligationId}", method = RequestMethod.GET)
  public Obligations findObligation(@PathVariable("obligationId") Integer obligationId){
    return obligationService.findOblId(obligationId);
  }
}
