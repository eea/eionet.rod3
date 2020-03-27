package eionet.rod.rest;

import eionet.rod.model.Spatial;
import eionet.rod.service.SpatialService;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Country rest controller.
 */
@RestController
@RequestMapping("/rest/country")
public class CountryRestController {

  @Autowired
  private SpatialService spatialService;

  /**
   * Find countries list.
   *
   * @return the list
   */
  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public List<Spatial> findAll(){
    return spatialService.findAll();
  }
}
