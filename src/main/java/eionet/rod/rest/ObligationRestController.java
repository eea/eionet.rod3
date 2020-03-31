package eionet.rod.rest;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.Spatial;
import eionet.rod.service.ObligationService;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
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
   * Find all opened obligations list.
   *
   * @return the list
   */
  @RequestMapping(value = "/findAllOpened", method = RequestMethod.GET)
  public List<Obligations> findAllOpenedObligations() {
    return obligationService.findObligationList(null, null, null, "N", null, null, null, null, false);
  }

  /**
   * Find opened obligations list with filters.
   *
   * @return the list
   */
  @RequestMapping(value = "/findOpened", method = RequestMethod.GET)
  public List<Obligations> findOpenedObligations(@RequestParam(value = "clientId",required = false) Integer clientId, @RequestParam(value="issueId", required = false) Integer issueId,
          @RequestParam(value="spatialId",required = false) Integer spatialId, @RequestParam(value="dateFrom",required = false) Date dateFrom, @RequestParam(value="dateTo",required = false) Date dateTo) {
    String deadlineCase = null;
    String date1 = null;
    String date2 = null;
    if (dateFrom != null && dateTo != null) {
      SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");  
      deadlineCase = "0";
      date1 = formatter.format(dateFrom);
      date2 = formatter.format(dateTo);
    }

    String client = Optional.ofNullable(clientId).map(id->id.toString()).orElse(null);
    String issue = Optional.ofNullable(issueId).map(id->id.toString()).orElse(null);
    String country = Optional.ofNullable(spatialId).map(id->id.toString()).orElse(null);

    return obligationService.findObligationList(client, issue, country, "N", deadlineCase, null, date1, date2, false);
  }
  
  /**
   * Find opened obligations obligations.
   *
   * @param obligationId the id
   *
   * @return the obligations
   */
  @RequestMapping(value = "/{obligationId}", method = RequestMethod.GET)
  public Obligations findObligation(@PathVariable("obligationId") Integer obligationId) {
    return obligationService.findOblId(obligationId);
  }

  /**
   * Find countries for an obligation.
   *
   * @param obligationId the id
   *
   * @return the list of countries
   */
  @RequestMapping(value = "/countries/{obligationId}", method = RequestMethod.GET)
  public List<Spatial> findObligationCountries(@PathVariable("obligationId") Integer obligationId, String voluntary) {
    return obligationService.findAllCountriesByObligation(obligationId, voluntary);
  }

  /**
   * Find issues for an obligation.
   *
   * @param obligationId the id
   *
   * @return the list of issues
   */
  @RequestMapping(value = "/issues/{obligationId}", method = RequestMethod.GET)
  public List<Issue> findObligationIssues(@PathVariable("obligationId") Integer obligationId) {
    return obligationService.findAllIssuesbyObligation(obligationId);
  }

  /**
   * Find clients for an obligation.
   *
   * @param obligationId the id
   *
   * @return the list of clients
   */
  @RequestMapping(value = "/clients/{obligationId}", method = RequestMethod.GET)
  public List<ClientDTO> findObligationClients(@PathVariable("obligationId") Integer obligationId) {
    return obligationService.findAllClientsByObligation(obligationId);
  }

  /**
   * Find obligations relations for an obligation.
   *
   * @param obligationId the id
   *
   * @return the obligations
   */
  @RequestMapping(value = "/relation/{obligationId}", method = RequestMethod.GET)
  public Obligations findObligationRelation(@PathVariable("obligationId") Integer obligationId) {
    return obligationService.findObligationRelation(obligationId);
  }
}
