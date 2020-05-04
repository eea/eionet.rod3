package eionet.rod.rest;

import static org.junit.Assert.*;

import eionet.rod.model.Spatial;
import eionet.rod.service.SpatialService;
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


public class CountryRestControllerTest {

  @InjectMocks
  private CountryRestController countryRestController;

  @Mock
  private SpatialService spatialService;

  @Before
  public void initMocks() {
    MockitoAnnotations.initMocks(this);
  }

  @Test
  public void findAll() {
    List<Spatial> spatials = new ArrayList<>();
    Spatial spatial = new Spatial();
    spatial.setSpatialId(1);
    spatials.add(spatial);
    Mockito.when(spatialService.findAll()).thenReturn(spatials);
    List<Spatial> result = countryRestController.findAll();
    Assert.assertNotNull(result);
    Assert.assertEquals(result.size(), 1);
    Assert.assertEquals(result.get(0).getSpatialId().intValue(), 1);
  }
}