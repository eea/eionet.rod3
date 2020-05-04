package eionet.rod.rest;

import eionet.rod.model.Issue;
import eionet.rod.service.IssueService;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * The type Issue rest controller.
 */
@RestController
@RequestMapping("/rest/issue")
public class IssueRestController {

  @Autowired
  private IssueService issueService;

  /**
   * Find issues list.
   *
   * @return the list
   */
  @RequestMapping(value = "/findAll", method = RequestMethod.GET)
  public List<Issue> findAll(){
    return issueService.findAllIssuesList();
  }
}
