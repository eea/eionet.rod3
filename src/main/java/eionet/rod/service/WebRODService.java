/*
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
 * Original Code: Kaido Laine (TietoEnator)
 */

package eionet.rod.service;

import eionet.rod.Constants;
import eionet.rod.model.Obligations;
import eionet.rod.model.Spatial;
import eionet.rod.util.RODServices;
import eionet.rod.util.RODUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import java.util.*;

/**
 * Container class for providing public services of WebROD through XML/RPC or SOAP.
 * 
 * @author Kaido Laine
 */
public class WebRODService {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebRODService.class);

    public WebRODService(){
        SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
    }

    private String valueOrDefault(String s, String defaultValue) {
        if (RODUtil.isNullOrEmpty(s)) {
            return defaultValue;
        } else {
            return s;
        }
    }

    /**
     * Returns Activity Ids and Titles.
     * 
     * @return Vector<Map<String, String>> (contains hashtables, one for each activity)
     * @throws ServiceException
     */
    public Vector<Map<String, String>> getActivities() throws ServiceException {
        Vector<Map<String, String>> result = new Vector<>();

        try {
            FileServiceIF fileService = RODServices.getFileService();

            String rodDomain  = fileService.getStringProperty( Constants.ROD_URL_DOMAIN );
            ObligationService obligationService = (ObligationService) SpringContext.getApplicationContext().getBean("obligationService");
            List<Obligations> obligationsList =  obligationService.findActivities();
            for(Obligations obligation : obligationsList) {
                Map<String, String> oblMap = new Hashtable<>();

                oblMap.put("terminated", (obligation.getTerminate().equalsIgnoreCase("Y")? "1" : "0"));
                oblMap.put("PK_RA_ID", obligation.getObligationId().toString());
                oblMap.put("SOURCE_TITLE", valueOrDefault(obligation.getSourceAlias(), ""));
                oblMap.put("details_url", rodDomain + "/obligations/" + obligation.getObligationId());
                oblMap.put("TITLE", valueOrDefault(obligation.getOblTitle(), ""));
                oblMap.put("uri", rodDomain + "/obligations/" + obligation.getObligationId());
                oblMap.put("LAST_UPDATE", valueOrDefault(obligation.getLastUpdate(), ""));
                oblMap.put("PK_SOURCE_ID", valueOrDefault(obligation.getSourceId(), ""));
                result.add(oblMap);
            }

        } catch (eionet.rod.util.exception.ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException("Internal server error " + e.getMessage());
        }

        return result;
    }

    /**
     * Returns all countries.
     * 
     * @return Vector<Map<String, String>> (contains hashtables, one for each record)
     * @throws ServiceException
     */
    public Vector<Map<String, String>> getCountries() throws ServiceException {
        Vector<Map<String, String>> result = new Vector<>();

        try {
            FileServiceIF fileService = RODServices.getFileService();

            SpatialService spatialService = (SpatialService) SpringContext.getApplicationContext().getBean("spatialService");

            List<Spatial> spatials = spatialService.findAll();
            for(Spatial s : spatials) {
                Map<String, String> oblMap = new Hashtable<>();
                result.add(oblMap);
                oblMap.put("iso", valueOrDefault(s.getTwoLetter().toUpperCase(), ""));
                oblMap.put("name", valueOrDefault(s.getName(), ""));
                oblMap.put("uri", fileService.getStringProperty(FileServiceIF.SPATIAL_NAMESPACE) + s.getSpatialId());
            }

        } catch (eionet.rod.util.exception.ServiceException e) {
            LOGGER.error(e.getMessage(), e);
            throw new ServiceException("Internal server error " + e.getMessage());
        }

        return result;

    }

}
