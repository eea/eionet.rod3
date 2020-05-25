package eionet.rod.rest;

import eionet.rod.model.Spatial;
import eionet.rod.service.SpatialService;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;


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