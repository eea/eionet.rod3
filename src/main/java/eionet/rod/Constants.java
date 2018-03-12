/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 *
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 *
 * The Original Code is "EINRC-4 / WebROD Project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Andre Karpistsenko (TietoEnator)
 */

package eionet.rod;

/**
 * <P>
 * Interface defining constants used in the WebROD system.
 * </P>
 * 
 * @author Andre Karpistsenko, Rando Valt
 * @version 1.1
 */

public interface Constants {

    public static final String ACL_RA_NAME = "/obligations";
    public static final String ACL_RO_NAME = "/obligations";
    public static final String ACL_LI_NAME = "/instruments";
    public static final String ACL_ADMIN_NAME = "/Admin";
    public static final String ACL_HARVEST_NAME = "/Admin/Harvest";
    public static final String ACL_CLIENT_NAME = "/Client";
    public static final String ACL_HELP_NAME = "/Admin/Helptext";

    public static final String ACL_VIEW_PERMISSION = "v";
    public static final String ACL_INSERT_PERMISSION = "i";
    public static final String ACL_UPDATE_PERMISSION = "u";
    public static final String ACL_DELETE_PERMISSION = "d";
    public static final String ACL_CONTROL_PERMISSION = "c";

    // index servlet constants
    public static final String INDEX_XSL = "index.xsl";
    public static final String INDEX_QUERY = "index.xrs";
    // reporting obligation browse servlet constants
    public static final String RORABROWSE_XSL = "rorabrowse.xsl";
    public static final String RORABROWSE_QUERY = "rorabrowse.xrs";
    // show servlet constants
    public static final String SOURCE_XSL = "source.xsl";
    public static final String SOURCE_QUERY = "source.xrs";
    public static final String HIERARCHY_XSL = "hierarchy.xsl";
    public static final String HIERARCHY_QUERY = "hierarchy.xrs";
    public static final String HIERARCHYX_XSL = "hierarchyx.xsl";

    public static final String ACTIVITY_XSL = "activity.xsl";
    public static final String ACTIVITY_QUERY = "activity.xrs";

    public static final String VERSIONS_QUERY = "versions.xrs";

    public static final String HISTORY_QUERY = "history.xrs";
    public static final String HARVESTING_HISTORY_QUERY = "harvesthistory.xrs";
    public static final String HISTORY_XSL = "history.xsl";
    public static final String ACTION_HIST_XSL = "actionhist.xsl";

    public static final String PARAMETERS_QUERY = "parameters.xrs";
    public static final String PARAMETERS_XSL = "parameters.xsl";

    // show mode constants
    public static final String SOURCE_MODE = "S";
    public static final String HIERARCHY_MODE = "C";
    public static final String HIERARCHYX_MODE = "X";
    public static final String ACTIVITY_MODE = "A";
    public static final String PARAMETERS_MODE = "M";

    // editor
    public static final String E_SOURCE_XSL = "esource.xsl";
    public static final String E_SOURCE_QUERY = "esource.xrs";
    public static final String E_REPORTING_XSL = "ereporting.xsl";
    public static final String E_REPORTING_QUERY = "ereporting.xrs";
    public static final String E_ACTIVITY_XSL = "eactivity.xsl";
    public static final String E_ACTIVITY_QUERY = "eactivity.xrs";

    public static final String ID_PARAM = "id";
    public static final String MODE_PARAM = "mode";
    public static final String AID_PARAM = "aid";
    public static final String ENTITY_PARAM = "entity";
    public static final String SV_PARAM = "sv";

    // Filter parameters for Reporting Obligation
    public static final String SHOWFILTER = "showfilters";
    public static final String ENV_ISSUE_FILTER = "env_issue";
    public static final String COUNTRY_FILTER = "country";
    public static final String RIVER_FILTER = "river";
    public static final String SEA_FILTER = "sea";
    public static final String LAKE_FILTER = "lake";
    public static final String PARAM_GROUP_FILTER = "param_group";
    public static final String ROTYPE_FILTER = "type";
    public static final String SOURCE_FILTER = "source";
    public static final String CLIENT_FILTER = "client";
    public static final String TERMINATED_FILTER = "terminated";

    // Spatial attribute types
    public static final String SPATIAL_COUNTRY = "C";
    public static final String SPATIAL_SEA = "S";
    public static final String SPATIAL_RIVER = "R";
    public static final String SPATIAL_LAKE = "L";
    public static final String SPATIAL_RESERVOIR = "O";

    /** */
    public static final int SEVERITY_INFO = 1;
    public static final int SEVERITY_CAUTION = 2;
    public static final int SEVERITY_WARNING = 3;
    public static final int SEVERITY_VALIDATION = 4;

    // Prop names
    static final String ROD_URL_EVENTS = "rod.url.events";
    // static final String ROD_URL_OBLIGATIONS ="rod.url.obligations";
    static final String ROD_URL_ACTIVITIES = "rod.url.activities";
    static final String ROD_URL_INSTRUMENTS = "rod.url.instruments";

    /** Specifies session attribute name where last action URL is kept. */
    static final String LAST_ACTION_URL_SESSION_ATTR = "ActionEventInterceptor#lastActionUrl";

    /**
     * Properties file name
     */

    public static final String PROP_FILE = "rod";

    /**
     * Parameter in the props file for LI namespace
     */
    public static final String ROD_LI_NS = "instruments.namespace";

    /**
     * Parameter in the props file for CL namespace
     */
    public static final String ROD_CL_NS = "clients.namespace";

    /**
     * Parameter in the props file for issues namespace
     */
    public static final String ROD_ISSUES_NS = "issues.namespace";

    /**
     * Parameter in the props file for the domain of WebROD
     */
    public static final String ROD_URL_NS = "rod.url.namespace";
    /**
*
*/
    public static final String ROD_URL_RO_NS = "rod.url.ro_namespace";

    /**
     * Parameter in the props file for the domain of WebROD
     */
    public static final String ROD_URL_DOMAIN = "rod.url.domain";

    /**
     * Servlet name showing RA
     */
    public static final String URL_SERVLET = "/obligations";
    /**
     * RA ID in Url
     */
    public static final String URL_ACTIVITY_ID = "id";

    /**
     * AID for RA in url
     */
    public static final String URL_ACTIVITY_AID = "aid";

    /**
     * Mode, for RA Mode=A in url
     */

    public static final String URL_ACTIVITY_AMODE = "mode=A";

    /**
     * Mode, for RA Mode=R in url
     */

    public static final String URL_ACTIVITY_RMODE = "mode=R";

    /**
     * FiledName for timestamp
     */

    public static final String TIMESTAMP_FILEDNAME = "LAST_UPDATE";

}
