package eionet.rod.util;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.StringTokenizer;

public class RODUtil {
	
	
	 /**
     * Returns the result of {@link #replaceTags(String, boolean, boolean)}, passing on the given string,
     * and setting the booleans to false.
     *
     * @param in Passed on.
     * @return String The result.
     */
    public static String replaceTags(String in) {
        return replaceTags(in, false, false);
    }

    /**
     * Returns the result of {@link #replaceTags(String, boolean, boolean)}, passing on the given string and boolean,
     * and setting the last boolean to false.
     *
     * @param in Passed on.
     * @param dontCreateHTMLAnchors Passed on.
     * @return String The result.
     */
    public static String replaceTags(String in, boolean dontCreateHTMLAnchors) {
        return replaceTags(in, dontCreateHTMLAnchors, false);
    }

    /**
     * Processes given text for display in HTML pages.
     *
     * @param in Text to process.
     * @param dontCreateHTMLAnchors If true, no HTML links will be interpreted.
     * @param dontCreateHTMLLineBreaks If true, no HTML line-breaks will be interpreted.
     * @return The result.
     */
	public static String replaceTags(String in, boolean dontCreateHTMLAnchors, boolean dontCreateHTMLLineBreaks) {

        in = (in != null ? in : "");
        StringBuffer ret = new StringBuffer();
        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '<') {
                ret.append("&lt;");
            } else if (c == '>') {
                ret.append("&gt;");
            } else if (c == '"') {
                ret.append("&quot;");
            } else if (c == '\'') {
                ret.append("&#039;");
            } else if (c == '\\') {
                ret.append("&#092;");
            } else if (c == '&') {
                boolean startsEscapeSequence = false;
                int j = in.indexOf(';', i);
                if (j > 0) {
                    String s = in.substring(i, j + 1);
                    UnicodeEscapes unicodeEscapes = new UnicodeEscapes();
                    if (unicodeEscapes.isXHTMLEntity(s) || unicodeEscapes.isNumericHTMLEscapeCode(s)) {
                        startsEscapeSequence = true;
                    }
                }

                if (startsEscapeSequence) {
                    ret.append(c);
                } else {
                    ret.append("&amp;");
                }
            } else if (c == '\n' && dontCreateHTMLLineBreaks == false) {
                ret.append("<br/>");
            } else if (c == '\r' && in.charAt(i + 1) == '\n' && dontCreateHTMLLineBreaks == false) {
                ret.append("<br/>");
                i = i + 1;
            } else {
                ret.append(c);
            }
        }

        String retString = ret.toString();
        if (dontCreateHTMLAnchors == false) {
            retString = setAnchors(retString, false, 50);
        }

        return retString;
    }
	
	 /**
     * Finds all URLs in a given string and replaces them with HTML anchors. If boolean newWindow==true then target will be a new
     * window, else no. If boolean cutLink>0 then cut the displayed link length.
     *
     * @param s String to parse.
     * @param newWindow Generate or not the "target=_blank" attribute.
     * @param cutLink Cut links display text to this length, replace the tail with 3 dots.
     * @return The resulting text.
     */
    public static String setAnchors(String s, boolean newWindow, int cutLink) {

        StringBuffer buf = new StringBuffer();

        StringTokenizer st = new StringTokenizer(s, " \t\n\r\f|(|)<>", true);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (!isURL(token)) {
                buf.append(token);
            } else {
                StringBuffer _buf = new StringBuffer("<a ");
                if (newWindow) {
                    _buf.append("target=\"_blank\" ");
                }
                _buf.append("href=\"");
                _buf.append(token);
                _buf.append("\">");

                if (cutLink < token.length()) {
                    _buf.append(token.substring(0, cutLink)).append("...");
                } else {
                    _buf.append(token);
                }

                _buf.append("</a>");
                buf.append(_buf.toString());
            }
        }

        return buf.toString();
    }
    
    /**
     * Finds all URLs in a given string and replaces them with HTML anchors. If boolean newWindow==true then target will be a new
     * window, else no.
     *
     * This method calls {@link #setAnchors(String, boolean, int)}, passing on the string and the boolean, and setting int=0.
     *
     * @param s String to parse.
     * @param newWindow Generate or not the "target=_blank" attribute.
     * @return The resulting text.
     */
    public static String setAnchors(String s, boolean newWindow) {

        return setAnchors(s, newWindow, 0);
    }

    /**
     * Calls {@link #setAnchors(String, boolean)}, passing on the string and giving true for the boolean.
     *
     * @param s Passed on.
     * @return String The parsed string.
     */
    public static String setAnchors(String s) {

        return setAnchors(s, true);
    }

    /**
     * Checks if the given string is a well-formed URL
     *
     * @param s The given string.
     * @return boolean True/false.
     */
    public static boolean isURL(String s) {
        try {
            URL url = new URL(s);
            return url != null;
        } catch (MalformedURLException e) {
            return false;
        }
    }
    
      
    /**
     * Expects given string to be in date format like "dd/mm/yyyy", and returns corresponding value in the MySQL format:
     * yyyy-mm-dd. Returns "NULL" if string is null or empty.
     *
     * @param date The string to parse.
     * @return The result.
     */
    public static String str2Date(String date) {
        if (RODUtil.isNullOrEmpty(date)) {
            return "NULL";
        }

        int len = date.length();

        // formats the input string in the form dd/mm/yyyy to MySQL date format yyyy-mm-dd
        //
        // 0123456789
        // WebROD format: dd/mm/yyyy
        // MySQL format: yyyy-mm-dd
        //

        if (len == 10) {
            char d1 = date.charAt(0);
            char d2 = date.charAt(1);
            char m1 = date.charAt(3);
            char m2 = date.charAt(4);
            char y1 = date.charAt(6);
            char y2 = date.charAt(7);
            char y3 = date.charAt(8);
            char y4 = date.charAt(9);
            char s1 = date.charAt(2);
            char s2 = date.charAt(5);

            if (Character.isDigit(d1) && Character.isDigit(d2) && Character.isDigit(m1) && Character.isDigit(m2)
                    && Character.isDigit(y1) && Character.isDigit(y2) && Character.isDigit(y3) && Character.isDigit(y4)
                    && s1 == '/' && s2 == '/') {
                StringBuffer ret = new StringBuffer(10);
                ret.insert(0, y1).insert(1, y2).insert(2, y3).insert(3, y4).insert(4, '-').insert(5, m1).insert(6, m2)
                        .insert(7, '-').insert(8, d1).insert(9, d2);

                return ret.toString();
            }
        }

        return "";
    }

    /**
     * Returns true if the given string is null or empty.
     *
     * @param s The string.
     * @return boolean Is or not.
     */
    public static boolean isNullOrEmpty(String s) {
        return s == null || s.length() == 0;
    }
    
    /**
     * Surrounds given string with apostrophes and also escapes any apostrophes inside the text for SQL.
     *
     * @param in The text to parse.
     * @return The result.
     */
    public static String strLiteral(String in) {
        in = (in != null ? in : "");
        StringBuffer ret = new StringBuffer("'");

        for (int i = 0; i < in.length(); i++) {
            char c = in.charAt(i);
            if (c == '\'') {
                ret.append("''");
            } else {
                ret.append(c);
            }
        }
        ret.append('\'');

        return ret.toString();
    }
}
