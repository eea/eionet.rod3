/**
 * The contents of this file are subject to the Mozilla Public
 * License Version 1.1 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a copy of
 * the License at http://www.mozilla.org/MPL/
 * <p>
 * Software distributed under the License is distributed on an "AS
 * IS" basis, WITHOUT WARRANTY OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * rights and limitations under the License.
 * <p>
 * The Original Code is "EINRC-4 / WebROD Project".
 * <p>
 * The Initial Developer of the Original Code is TietoEnator.
 * The Original Code code was developed for the European
 * Environment Agency (EEA) under the IDA/EINRC framework contract.
 * <p>
 * Copyright (C) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 * <p>
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

    int SEVERITY_WARNING = 3;

    String ROD_URL_DOMAIN = "rod.url.domain";

    int BREADCRUMB_MAX_LEN = 25;
    int TEXT_MAX_LEN = 400;
    int TRUNCATE_DEFAULT_LEN = 80;

}
