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
 * The Original Code is "NaMod project".
 *
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 *
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.rod.extractor;

import eionet.rpcclient.ServiceClientIF;

/**
 * Constant values for external services, methods exc
 */
public interface ExtractorConstants {

    // default type for the remote services
    public final int SERVICE_CLIENT_TYPE = ServiceClientIF.CLIENT_TYPE_XMLRPC;

    public final String PROP_FILE = "rod";
    public final String LOG_FILE = "extractorlog.txt";

    public final String SYSTEM_USER = "ROD - system";

    public final String DD_SRV_NAME = "DataDictService";
    public final String DD_GETPARAMS_METHOD = "getParameters";

}