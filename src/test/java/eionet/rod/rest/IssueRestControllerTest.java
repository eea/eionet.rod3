package eionet.rod.rest;

import eionet.rod.model.Issue;
import eionet.rod.service.IssueService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


public class IssueRestControllerTest {

  @InjectMocks
  private IssueRestController issueRestController;

  @Mock
  private IssueService issueService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }
  
  @Test
  public void findAll() {
    List<Issue> issues = new ArrayList<>();
    Issue issue = new Issue();
    issue.setIssueId(1);
    issues.add(issue);
    Mockito.when(issueService.findAllIssuesList()).thenReturn(issues);
    List<Issue> result = issueRestController.findAll();
    Assert.assertNotNull(result);
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).getIssueId().intValue(), 1);
  }
}