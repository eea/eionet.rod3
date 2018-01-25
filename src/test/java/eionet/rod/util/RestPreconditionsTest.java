package eionet.rod.util;

import static org.junit.Assert.assertNotNull;


import org.junit.Test;


public class RestPreconditionsTest {

	@Test
    public void instanticateClass() {
        RestPreconditions obj = new RestPreconditions();
        assertNotNull(obj);
    }
		
}
