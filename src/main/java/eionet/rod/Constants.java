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

    String ACL_RA_NAME = "/obligations";
    String ACL_RO_NAME = "/obligations";
    String ACL_LI_NAME = "/instruments";
    String ACL_ADMIN_NAME = "/Admin";
    String ACL_HARVEST_NAME = "/Admin/Harvest";
    String ACL_CLIENT_NAME = "/Client";
    String ACL_HELP_NAME = "/Admin/Helptext";

    String ACL_VIEW_PERMISSION = "v";
    String ACL_INSERT_PERMISSION = "i";
    String ACL_UPDATE_PERMISSION = "u";
    String ACL_DELETE_PERMISSION = "d";
    String ACL_CONTROL_PERMISSION = "c";

    // index servlet constants
    String INDEX_XSL = "index.xsl";
    String INDEX_QUERY = "index.xrs";
    // reporting obligation browse servlet constants
    String RORABROWSE_XSL = "rorabrowse.xsl";
    String RORABROWSE_QUERY = "rorabrowse.xrs";
    // show servlet constants
    String SOURCE_XSL = "source.xsl";
    String SOURCE_QUERY = "source.xrs";
    String HIERARCHY_XSL = "hierarchy.xsl";
    String HIERARCHY_QUERY = "hierarchy.xrs";
    String HIERARCHYX_XSL = "hierarchyx.xsl";

    String ACTIVITY_XSL = "activity.xsl";
    String ACTIVITY_QUERY = "activity.xrs";

    String VERSIONS_QUERY = "versions.xrs";

    String HISTORY_QUERY = "history.xrs";
    String HARVESTING_HISTORY_QUERY = "harvesthistory.xrs";
    String HISTORY_XSL = "history.xsl";
    String ACTION_HIST_XSL = "actionhist.xsl";

    String PARAMETERS_QUERY = "parameters.xrs";
    String PARAMETERS_XSL = "parameters.xsl";

    // show mode constants
    String SOURCE_MODE = "S";
    String HIERARCHY_MODE = "C";
    String HIERARCHYX_MODE = "X";
    String ACTIVITY_MODE = "A";
    String PARAMETERS_MODE = "M";

    // editor
    String E_SOURCE_XSL = "esource.xsl";
    String E_SOURCE_QUERY = "esource.xrs";
    String E_REPORTING_XSL = "ereporting.xsl";
    String E_REPORTING_QUERY = "ereporting.xrs";
    String E_ACTIVITY_XSL = "eactivity.xsl";
    String E_ACTIVITY_QUERY = "eactivity.xrs";

    String ID_PARAM = "id";
    String MODE_PARAM = "mode";
    String AID_PARAM = "aid";
    String ENTITY_PARAM = "entity";
    String SV_PARAM = "sv";

    // Filter parameters for Reporting Obligation
    String SHOWFILTER = "showfilters";
    String ENV_ISSUE_FILTER = "env_issue";
    String COUNTRY_FILTER = "country";
    String RIVER_FILTER = "river";
    String SEA_FILTER = "sea";
    String LAKE_FILTER = "lake";
    String PARAM_GROUP_FILTER = "param_group";
    String ROTYPE_FILTER = "type";
    String SOURCE_FILTER = "source";
    String CLIENT_FILTER = "client";
    String TERMINATED_FILTER = "terminated";

    // Spatial attribute types
    String SPATIAL_COUNTRY = "C";
    String SPATIAL_SEA = "S";
    String SPATIAL_RIVER = "R";
    String SPATIAL_LAKE = "L";
    String SPATIAL_RESERVOIR = "O";

    /** */
    int SEVERITY_INFO = 1;
    int SEVERITY_CAUTION = 2;
    int SEVERITY_WARNING = 3;
    int SEVERITY_VALIDATION = 4;

    // Prop names
    String ROD_URL_EVENTS = "rod.url.events";
    // static final String ROD_URL_OBLIGATIONS ="rod.url.obligations";
    String ROD_URL_ACTIVITIES = "rod.url.activities";
    String ROD_URL_INSTRUMENTS = "rod.url.instruments";

    /** Specifies session attribute name where last action URL is kept. */
    String LAST_ACTION_URL_SESSION_ATTR = "ActionEventInterceptor#lastActionUrl";

    /**
     * Properties file name
     */

    String PROP_FILE = "rod";

    /**
     * Parameter in the props file for LI namespace
     */
    String ROD_LI_NS = "instruments.namespace";

    /**
     * Parameter in the props file for CL namespace
     */
    String ROD_CL_NS = "clients.namespace";

    /**
     * Parameter in the props file for issues namespace
     */
    String ROD_ISSUES_NS = "issues.namespace";

    /**
     * Parameter in the props file for the domain of WebROD
     */
    String ROD_URL_NS = "rod.url.namespace";
    /**
*
*/
    String ROD_URL_RO_NS = "rod.url.ro_namespace";

    /**
     * Parameter in the props file for the domain of WebROD
     */
    String ROD_URL_DOMAIN = "rod.url.domain";

    /**
     * Servlet name showing RA
     */
    String URL_SERVLET = "/obligations";
    /**
     * RA ID in Url
     */
    String URL_ACTIVITY_ID = "id";

    /**
     * AID for RA in url
     */
    String URL_ACTIVITY_AID = "aid";

    /**
     * Mode, for RA Mode=A in url
     */

    String URL_ACTIVITY_AMODE = "mode=A";

    /**
     * Mode, for RA Mode=R in url
     */

    String URL_ACTIVITY_RMODE = "mode=R";

    /**
     * FiledName for timestamp
     */

    String TIMESTAMP_FILEDNAME = "LAST_UPDATE";

    int BREADCRUMB_MAX_LEN = 25;
    int TEXT_MAX_LEN =  400;
    int TRUNCATE_DEFAULT_LEN = 80;

}
