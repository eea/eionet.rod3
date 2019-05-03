package eionet.rod.model;

import eionet.rod.Constants;
import eionet.rod.util.RODUtil;


/**
 * This class implements one breadcrumb.
 */
public class BreadCrumb {

    /**
     * link to place in hierarchy. Can be null for last crumb.
     */
    private String link;
    /**
     * The text for the link.
     */
    private String linktext;

    public BreadCrumb(String linktext) {
        this.link = null;
        this.linktext = linktext;
    }

    public BreadCrumb(String link, String linktext) {
        this.link = link;
        this.linktext = linktext;
    }

    public String getLink() {
        return link;
    }

    /**
     * Return the breadcrumb link text, but truncate it if it is longer than 25 characters.
     */
    public String getLinktext() {
        if (linktext == null || "".equals(linktext)) {
            return "Unknown";
        } else {
            return RODUtil.truncateText(linktext, Constants.BREADCRUMB_MAX_LEN);
        }
    }

}
