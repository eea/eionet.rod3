package eionet.rod.model;

import eionet.rod.util.UnicodeEscapes;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class UnicodeEscapesTest {

    @Test
    public void instanticateClass() {
        UnicodeEscapes obj = new UnicodeEscapes();
        Assert.assertNotNull(obj);
    }

    @Test
    public void test_getDecimal() {
        UnicodeEscapes obj = new UnicodeEscapes();
        assertEquals(obj.getDecimal("ipwuqhtgirej"), -1);
        assertEquals(obj.getDecimal("euro"), 8364);
    }

    @Test
    public void test_isXHTMLEntity() {
        UnicodeEscapes obj = new UnicodeEscapes();
        assertFalse(obj.isXHTMLEntity(null));
        assertFalse(obj.isXHTMLEntity("lsquo"));
        assertFalse(obj.isXHTMLEntity("&;"));
        assertTrue(obj.isXHTMLEntity("&lsquo;"));
    }

    @Test
    public void test_isNumericHTMLEscapeCode() {
        UnicodeEscapes obj = new UnicodeEscapes();
        assertFalse(obj.isNumericHTMLEscapeCode(""));
        assertFalse(obj.isNumericHTMLEscapeCode("160"));
        assertFalse(obj.isNumericHTMLEscapeCode("&160;"));
        assertFalse(obj.isNumericHTMLEscapeCode("&#;"));
        assertFalse(obj.isNumericHTMLEscapeCode("&#a160;"));
        assertTrue(obj.isNumericHTMLEscapeCode("&#160;"));
    }

}
