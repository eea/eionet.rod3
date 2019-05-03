package eionet.rod.model;

import org.junit.Assert;
import org.junit.Test;

public class InstrumentClassificationDTOTest {
    @Test
    public void instanticateClass() {
        InstrumentClassificationDTO obj = new InstrumentClassificationDTO();
        Assert.assertNotNull(obj);
    }
}