package eionet.rod.model;

import org.junit.Test;

import static org.junit.Assert.*;


/**
 *
 */
public class BreadCrumbTest {

    @Test
    public void instanticateClass() {
        BreadCrumb obj = new BreadCrumb("link", "Link test");
        assertNotNull(obj);
        assertEquals("Link test", obj.getLinktext());
    }

    @Test
    public void longLinkText() {
        BreadCrumb obj = new BreadCrumb("link", "supercalifragilisticexpialidocious");
        assertEquals("supercalifragilisticexpi...", obj.getLinktext());
    }

    @Test
    public void nullLinkText() {
        BreadCrumb obj = new BreadCrumb("link", null);
        assertEquals("link", obj.getLink());
        assertEquals("Unknown", obj.getLinktext());
    }

    @Test
    public void nullLink() {
        BreadCrumb obj = new BreadCrumb("supercalifragilisticexpialidocious");
        assertNull(obj.getLink());
        assertEquals("supercalifragilisticexpi...", obj.getLinktext());
    }

} 
