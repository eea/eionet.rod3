package eionet.rod.model;

import org.junit.Assert;
import org.junit.Test;

public class InstrumentDTOTest {
    @Test
    public void instanticateClass() {
        InstrumentDTO obj = new InstrumentDTO();
        Assert.assertNotNull(obj);
    }
}