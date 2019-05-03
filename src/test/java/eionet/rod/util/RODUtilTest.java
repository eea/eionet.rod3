package eionet.rod.util;

import org.junit.Test;

import java.util.Calendar;
import java.util.Date;

import static org.junit.Assert.*;

/**
 * @author Jaanus Heinlaid
 */
public class RODUtilTest {

    @Test
    public void test_replaceTags() {
        assertEquals(
                "<a href=\"http://cdr.eionet.europa.eu/search?y=1&amp;z=2\">http://cdr.eionet.europa.eu/search?y=1&amp;z=2</a>",
                RODUtil.replaceTags("http://cdr.eionet.europa.eu/search?y=1&z=2"));

        // Simple &
        assertEquals("Fruit &amp; Vegetables", RODUtil.replaceTags("Fruit & Vegetables"));

        // Simple & with ; appended
        assertEquals("Fruit &amp;; Vegetables", RODUtil.replaceTags("Fruit &; Vegetables"));

        // Long decimal entity (This is the € sign)
        assertEquals("Grand total: &#8364; 50.000", RODUtil.replaceTags("Grand total: &#8364; 50.000"));

        // Simple hex entity
        assertEquals("Fruit &amp;#x26; Vegetables", RODUtil.replaceTags("Fruit &#x26; Vegetables"));

        // Long hexadecimal entity (This is the € sign)
        assertEquals("Grand total: &amp;#x20AC; 50.000", RODUtil.replaceTags("Grand total: &#x20AC; 50.000"));

        // Already encoded
        assertEquals("Fruit &amp; Vegetables", RODUtil.replaceTags("Fruit &amp; Vegetables"));

        // Unknown entity
        assertEquals("Fruit &amp;unknown; Vegetables", RODUtil.replaceTags("Fruit &unknown; Vegetables"));

        // Unusual entity
        assertEquals("Fruit &euro; Vegetables", RODUtil.replaceTags("Fruit &euro; Vegetables"));

        // Test newline
        assertEquals("Fruit<br/>Vegetables", RODUtil.replaceTags("Fruit\nVegetables"));

        // Don't create anchors = true
        assertEquals("http://cdr.eionet.europa.eu/search?y=1&amp;z=7",
                RODUtil.replaceTags("http://cdr.eionet.europa.eu/search?y=1&z=7", true));

        // Test Unicode char
        assertEquals("€", RODUtil.replaceTags("€"));

        // Test HTML tags
        assertEquals("&lt;div class=&#039;Apostrophs&#039;&gt;", RODUtil.replaceTags("<div class='Apostrophs'>"));
        assertEquals("&lt;div class=&quot;Quotes&quot;&gt;", RODUtil.replaceTags("<div class=\"Quotes\">"));
        assertEquals("&lt;a href=&quot;http://cnn.org/&quot;&gt;CNN&lt;/a&gt;",
                RODUtil.replaceTags("<a href=\"http://cnn.org/\">CNN</a>"));
    }

    /*
     * This test documents an actual bug in the system.
     */
    @Test
    public void test_replaceTagsMultiLine() {
        String input =
                "The templates (XML schema) and the reporting manual are available"
                        + " at: http://www.eionet.europa.eu/schemas/eprtr\n\nTemplate for resubmissions is"
                        + " available at: http://circa.europa.eu/Public/irc/env/e_prtr/library?l=/e-prtr_re-delivery/resubmissionxls/_EN_1.0_&a=i";

        String expected =
                "The templates (XML schema) and the reporting manual are available"
                        + " at: <a href=\"http://www.eionet.europa.eu/schemas/eprtr\">http://www.eionet.e"
                        + "uropa.eu/schemas/eprtr</a><br/><br/>Template for resubmissions is"
                        + " available at: <a href=\"http://circa.europa.eu/Public/irc/env/e_prtr/library?"
                        + "l=/e-prtr_re-delivery/resubmissionxls/_EN_1.0_&amp;a=i\">http://circa.europa.eu"
                        + "/Public/irc/env/e_prtr/libra...</a>";

        String result = RODUtil.replaceTags(input);
        assertEquals(expected, result);
    }


    @Test
    public void test_isURL() {
        assertTrue(RODUtil.isURL("http://cdr.eionet.europa.eu/"));
        assertTrue(RODUtil.isURL("ftp://ftp.eionet.europa.eu/"));

        assertFalse(RODUtil.isURL("XXX"));
    }


    @Test
    public void test_setAnchors() {
        // Simple check
        assertEquals("<a href=\"http://en.wikipedia.org/wiki/Fahrvergnügen\">http://en....</a>",
                RODUtil.setAnchors("http://en.wikipedia.org/wiki/Fahrvergnügen", false, 10));

        // Check with popup
        assertEquals("<a target=\"_blank\" href=\"http://en.wikipedia.org/wiki/Fahrvergnügen\">http://en....</a>",
                RODUtil.setAnchors("http://en.wikipedia.org/wiki/Fahrvergnügen", true, 10));

        // setAnchors doesn't escape &-signs
        // Setting cutlinks to 0 does not do what the documentation says it does
        assertEquals("<a href=\"http://cdr.eionet.europa.eu/search?y=1&z=2\">...</a>",
                RODUtil.setAnchors("http://cdr.eionet.europa.eu/search?y=1&z=2", false, 0));
    }

    @Test
    public void test_setAnchorsInParenthesis() {
        assertEquals(
                "Some text (<a href=\"http://en.wikipedia.org/wiki/Fahrvergnügen\">http://en.wikipedia.org/wiki/Fahrvergnügen</a>).",
                RODUtil.setAnchors("Some text (http://en.wikipedia.org/wiki/Fahrvergnügen).", false, 100));

        assertEquals(
                "Some text (<a href=\"http://en.wikipedia.org/wiki/Fahrvergnügen\">http://en.wikipedia.org/wiki/Fahrvergnügen</a> ).",
                RODUtil.setAnchors("Some text (http://en.wikipedia.org/wiki/Fahrvergnügen ).", false, 100));

    }

    @Test
    public void truncateText() {
        assertEquals("National Authorities, International Organisations, Public, Scientific Community...", RODUtil.truncateText("National Authorities, International Organisations, Public, Scientific Community etc"));
        assertEquals("Bonn Agreement Secretariat", RODUtil.truncateText("Bonn Agreement Secretariat"));
    }

    @Test
    public void isNullorEmptyText() {
        assertTrue(RODUtil.isNullOrEmpty(null));
        assertFalse(RODUtil.isNullOrEmpty("Bonn Agreement Secretariat"));
    }

    @Test
    public void miliseconds2DateTest() {
        long ts = 1519733894881L;

        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(ts);
        assertEquals(c.getTime(), RODUtil.miliseconds2Date(ts));
    }

    @Test
    public void testReadDate() {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.HOUR_OF_DAY, 0);
        c.set(Calendar.MINUTE, 0);
        c.set(Calendar.SECOND, 0);
        c.set(Calendar.MILLISECOND, 0);
        c.set(Calendar.DAY_OF_MONTH, 21);
        c.set(Calendar.MONTH, Calendar.MARCH);
        c.set(Calendar.YEAR, 2019);
        Date theDate = c.getTime();
        assertEquals(theDate, RODUtil.readDate("21/03/2019"));
        assertEquals(theDate, RODUtil.readDate("21-03-2019"));
        assertEquals(theDate, RODUtil.readDate("2019/03/21"));
        assertEquals(theDate, RODUtil.readDate("2019-03-21"));
        assertNull(RODUtil.readDate("10-02/2019"));
        assertNull(RODUtil.readDate("10/02-2019"));
        assertNull(RODUtil.readDate("10022019"));
        assertNull(RODUtil.readDate("1002"));
        assertNull(RODUtil.readDate("10-May-2019"));
    }

}
