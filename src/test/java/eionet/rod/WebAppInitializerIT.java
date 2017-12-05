package eionet.rod;

import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


/**
 *
 */
public class WebAppInitializerIT {

    @Test
    public void instanticateClass() {
        WebAppInitializer obj = new WebAppInitializer();
        assertNotNull(obj);
    }

} 
