package eionet.rod.rest;


import eionet.rod.model.Obligations;
import eionet.rod.service.ObligationService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
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
  @RequestMapping(value = "/findOpenened", method = RequestMethod.GET)
  public List<Obligations> findOpenedObligations(){
    return obligationService.findObligationList(null,null,null,"N",null,null,null,null,false);
  }

  /**
   * Find opened obligations obligations.
   *
   * @param id the id
   *
   * @return the obligations
   */
  @RequestMapping(value = "/id", method = RequestMethod.GET)
  public Obligations findOpenedObligations(@RequestParam(value = "id") Integer id){
    return obligationService.findOblId(id);
  }
}
