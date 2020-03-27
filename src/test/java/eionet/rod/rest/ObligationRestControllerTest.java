package eionet.rod.rest;

import static org.junit.Assert.*;

import eionet.rod.model.Obligations;
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
    List<Obligations> obligations=new ArrayList<>();
    Obligations obligation = new Obligations();
    obligation.setObligationId(1);
    obligations.add(obligation);
    Mockito.when(obligationService.findObligationList(null,null,null,"N",null,null,null,null,false)).thenReturn(obligations);
    List<Obligations> result=obligationRestController.findOpenedObligations();
    Assert.assertNotNull(result);
    Assert.assertEquals(result.size(),1);
    Assert.assertEquals(result.get(0).getObligationId().intValue(),1);
  }


  @Test
  public void findOpenedObligations1() {
    Obligations obligation = new Obligations();
    obligation.setObligationId(1);
    Mockito.when(obligationService.findOblId(1)).thenReturn(obligation);
    Obligations result=obligationRestController.findOpenedObligations(1);
    Assert.assertNotNull(result);
    Assert.assertEquals(result.getObligationId().intValue(),1);
  }
}