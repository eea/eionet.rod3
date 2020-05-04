package eionet.rod.rest;

import static org.junit.Assert.*;

import eionet.rod.model.ClientDTO;
import eionet.rod.model.Issue;
import eionet.rod.model.Obligations;
import eionet.rod.model.Spatial;
import eionet.rod.service.ObligationService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;


public class ObligationRestControllerTest {

  @InjectMocks
  private ObligationRestController obligationRestController;

  @Mock
  private ObligationService obligationService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }
  @Test
  public void findOpenedObligations() {
    List<Obligations> obligations = new ArrayList<>();
    Obligations obligation = new Obligations();
    obligation.setObligationId(1);
    obligations.add(obligation);
    Mockito.when(obligationService.findObligationList(null, null, null, "N", null, null, null, null, false)).thenReturn(obligations);
    List<Obligations> result = obligationRestController.findOpenedObligations(null, null, null, null, null);
    List<Obligations> result2 = obligationRestController.findAllOpenedObligations();
    Assert.assertNotNull(result);
    Assert.assertNotNull(result2);
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result2.size(), 1);
    Assert.assertEquals(result.get(0).getObligationId().intValue(), 1);
    Assert.assertEquals(result2.get(0).getObligationId().intValue(), 1);
  }

  @Test
  public void findObligation() {
    Obligations obligation = new Obligations();
    obligation.setObligationId(1);
    Mockito.when(obligationService.findOblId(1)).thenReturn(obligation);
    Obligations result = obligationRestController.findObligation(1);
    Assert.assertNotNull(result);
    Assert.assertEquals(result.getObligationId().intValue(), 1);
  }

  @Test
  public void findObligationCountries() {
    List<Spatial> spatials = new ArrayList<>();
    Spatial spatial = new Spatial();
    spatial.setSpatialId(1);
    spatials.add(spatial);
    Mockito.when(obligationService.findAllCountriesByObligation(1, "Y")).thenReturn(spatials);
    List<Spatial> result = obligationRestController.findObligationCountries(1, "Y");
    Assert.assertNotNull(result);
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).getSpatialId().intValue(), 1);
  }

  @Test
  public void findObligationIssues() {
    List<Issue> issues = new ArrayList<>();
    Issue issue = new Issue();
    issue.setIssueId(1);
    issues.add(issue);
    Mockito.when(obligationService.findAllIssuesbyObligation(1)).thenReturn(issues);
    List<Issue> result = obligationRestController.findObligationIssues(1);
    Assert.assertNotNull(result);
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).getIssueId().intValue(), 1);
  }

  @Test
  public void findObligationClients() {
    List<ClientDTO> clients = new ArrayList<>();
    ClientDTO client = new ClientDTO();
    client.setClientId(1);
    clients.add(client);
    Mockito.when(obligationService.findAllClientsByObligation(1)).thenReturn(clients);
    List<ClientDTO> result = obligationRestController.findObligationClients(1);
    Assert.assertNotNull(result);
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).getClientId().intValue(), 1);
  }

  @Test
  public void findObligationRelation() {
    Obligations obligation = new Obligations();
    obligation.setObligationId(2);
    Mockito.when(obligationService.findObligationRelation(1)).thenReturn(obligation);
    Obligations result = obligationRestController.findObligationRelation(1);
    Assert.assertNotNull(result);
    Assert.assertEquals(result.getObligationId().intValue(), 2);
  }
}