package eionet.rod.util;

import org.junit.Test;

import static org.junit.Assert.assertNotNull;


public class RestPreconditionsTest {

    @Test
    public void instanticateClass() {
        RestPreconditions obj = new RestPreconditions();
        assertNotNull(obj);
    }

}
