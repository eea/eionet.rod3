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
 * Copyright (c) 2000-2002 by European Environment Agency.  All
 * Rights Reserved.
 *
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.rod.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception class for service layer error/exception situations.
 *
 * @author Rando Valt
 * @version 1.0
 */
public class ServiceException extends Exception {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceException.class);

    private static final long serialVersionUID = 1L;

    /**
     * Creates new <code>ServiceException</code> without detail message.
     */
    public ServiceException() {
    }

    /**
     * Constructs an <code>ServiceException</code> with the specified detail message.
     *
     * @param msg
     *            the detail message.
     */
    public ServiceException(String msg) {
        super(msg);
        LOGGER.error("Service exception occured with reason <<" + msg + ">>");
    }
}