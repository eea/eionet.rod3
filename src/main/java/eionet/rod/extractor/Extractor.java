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
 * Original Code: Ander Tenno (TietoEnator)
 */

package eionet.rod.extractor;

import eionet.directory.DirServiceException;
import eionet.directory.DirectoryService;
import eionet.rod.dao.UndoService;
import eionet.rod.model.Roles;
import eionet.rod.service.DeliveryService;
import eionet.rod.service.FileServiceIF;
import eionet.rod.service.ObligationService;
import eionet.rod.service.RoleService;
import eionet.rod.util.RODServices;
import eionet.rod.util.exception.ResourceNotFoundException;
import eionet.rod.util.exception.ServiceException;


import org.openrdf.query.QueryLanguage;
import org.openrdf.query.TupleQuery;
import org.openrdf.query.TupleQueryResult;
import org.openrdf.repository.RepositoryConnection;
//import org.openrdf.repository.RepositoryException;
import org.openrdf.repository.sparql.SPARQLRepository;

//import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

        import org.openrdf.query.BindingSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Pulls information from various services and saves it to DB.
 */
@Service(value = "extractorService")
@Transactional
public class Extractor implements ExtractorConstants {

    private static final Logger LOGGER = LoggerFactory.getLogger(Extractor.class);

    public static final int ALL_DATA = 0;
    public static final int DELIVERIES = 1;
    public static final int ROLES = 2;

    //private static FileServiceIF fileSrv = null;
    boolean debugLog = true;
    private static PrintWriter out = null;
   // private RODDaoFactory daoFactory;
   
    @Autowired
    RoleService roleService;
    @Autowired
    ObligationService obligationService;
    @Autowired
    DeliveryService deliveryService;
    @Autowired
    UndoService undoService;
    
    //@Autowired
    FileServiceIF fileSrv;
    
    private static Extractor extractor;

    public Extractor() {
    }

    /**
     * Called when script is run from the command-line. Takes one optional argument. The mode, which can be 0-3. Assumes 0 if not
     * provided.
     *
     * @param args
     *            command-line arguments
     */
    public static void main(String[] args) {

        try {
            String mode = null;
            String userName = SYSTEM_USER;

            if (args.length == 1) {
                mode = args[0];
            } else if (args.length > 1) {
                //this is feedback to the user not debugging as this class is executed in the cmd line
                //also log to file for other applications
                System.out.println("Usage: Extractor [mode]");
                LOGGER.error("Usage: Extractor [mode]");
                return;
            } else {
                mode = String.valueOf(ALL_DATA);
            }

            execute(Integer.parseInt(mode), userName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    public static void execute(int mode, String userName) {
        try {
            if (extractor == null) {
                extractor = new Extractor();
            }

            
            extractor.harvest(mode, userName);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
           // RODServices.sendEmail("Error in extractor", e.toString());
        }
    }
    private String cDT() {
        Date d = new Date();
        return "[" + d + "] ";
    }

    // Cleanup after everything is done
    //
    public void exitApp(boolean successful) {
        log(this.cDT() + "Extractor v1.0 - " + ((successful) ? "finished succesfully." : "failed to complete."));
        if (out != null) {
            out.flush();
            out.close();
        }
    }

    /**
     * Extract the data.
     *
     * @param mode
     * @param userName
     * @throws ServiceException
     */
    public void harvest(int mode, String userName) throws ServiceException  {

        // Initial set-up: create class; open the log file
        // mode, which data to harvest

//        String logPath = null;
//        String logfileName = null;
//               
        try {
        	fileSrv = RODServices.getFileService();
//            
        	debugLog = fileSrv.getBooleanProperty("extractor.debugmode");

        } catch (ServiceException e) {
            throw new ServiceException("Unable to get settings from properties file. The following error was reported:\n" + e, e);
        }

        //String actionText = "Harvesting - ";
        long a = System.currentTimeMillis();

        /***************************************************
         * Start extracting
         ***************************************************/

        // Get delivery list from Content Registry and save it also
        if (mode == ALL_DATA || mode == DELIVERIES) {
           // actionText += " deliveries ";
            extractDeliveries();
        }

        // Get roles from Eionet Directory and save them, too
        if (mode == ALL_DATA || mode == ROLES) {
            //actionText += " - roles ";
            try {
//
                StringBuilder errMsg = new StringBuilder();
                List<Roles> respRoles = obligationService.getRespRoles();

                // remove leftovers from previous harvest
                roleService.commitRoles();

                roleService.backUpRoles();

                for (Roles respRole : respRoles) {
                    try {
                        saveRole(respRole.getRoleName());
                    } catch (Exception e) {
                        errMsg.append('\n').append(e.getMessage());
                    }
                }

                roleService.commitRoles();

                if (debugLog) {
                    log("* Roles OK");
                }

                // todo use the errMsg somehow?
//                if (StringUtils.isNotBlank(errMsg.toString())) {
//                    RODServices.sendEmail("Error in Extractor ", errMsg.toString());
//                }
                
                // persons + org name

            } catch (Exception e) {
                log("Operation failed while filling the database from Eionet Directory. The following error was reported:\n"
                        + e.getMessage());
                LOGGER.error(e.getMessage(), e);
                exitApp(false); // return;
                throw new ResourceNotFoundException(
                        "Operation failed while filling the database from Eionet Directory. The following error was reported:\n"
                                + e.getMessage(), e);
            }
        } // mode includes roles

        long b = System.currentTimeMillis();

        log(" ** Harvesting successful TOTAL TIME = " + (b - a));

        exitApp(true);
    }

    /**
     *
     * @param s
     */
    private static void log(String s) {
        LOGGER.debug(s);
        if (out != null) {
            out.println(s);
        }
    }

    /**
     * Get Reportnet deliveries from the Content Registry using SPARQL. It first backs up the existing delivery information, then
     * executes the SPARQL query in chunks of 1000.
     *
     * @throws ServiceException
     */
    private void extractDeliveries() {
        log("Going to extract deliveries from CR");

        final String prefixDeclarations = "PREFIX dct: <http://purl.org/dc/terms/> " + "PREFIX rod: <http://rod.eionet.europa.eu/schema.rdf#> ";
        final String queryPattern = "SELECT DISTINCT ?link ?title ?locality ?obligation ?period ?date ?note WHERE { " +
                "_:subj a rod:Delivery; " +
                "rod:link ?link; " +
                "dct:title ?title; " +
                "rod:locality ?locality; " +
                "rod:obligation ?obligation; " +
                "rod:released ?date " +
                "OPTIONAL { _:subj rod:period ?period } " +
                "OPTIONAL { _:subj rod:coverageNote ?note }" +
                "}";

        final String query = prefixDeclarations + queryPattern;
        final String countQuery = prefixDeclarations + "SELECT (COUNT(*) AS ?count) {{" +  queryPattern + "}}";
        
        RepositoryConnection conn = null;
        
        try {
            String endpointURL = fileSrv.getStringProperty(FileServiceIF.CR_SPARQL_ENDPOINT);
            SPARQLRepository crEndpoint = new SPARQLRepository(endpointURL);
            crEndpoint.initialize();

            conn = crEndpoint.getConnection();

            int resultsSize = 0;
            TupleQueryResult countTupleQueryResult = conn.prepareTupleQuery(QueryLanguage.SPARQL, countQuery).evaluate();
            if (countTupleQueryResult.hasNext()) {
                BindingSet bindingSet = countTupleQueryResult.next();
                resultsSize = Integer.parseInt(bindingSet.getValue("count").stringValue());
            }

            if (resultsSize > 0) {
                HashMap<String, HashSet<Integer>> savedCountriesByObligationId = new HashMap<>();
                
                // back up currently existing deliveries
                deliveryService.backUpDeliveries();

                int chunkSize = 1000;
                int offset = 0;
                int saveCount = 0;
                //only for testing
                //resultsSize = 1001;
                
                while (offset <= resultsSize) {
                    String limitedQuery = query + " LIMIT " + chunkSize + " OFFSET " + offset;
                    TupleQuery q = conn.prepareTupleQuery(QueryLanguage.SPARQL, limitedQuery);
                    TupleQueryResult bindings = q.evaluate();

                    // increase offset
                    offset += chunkSize;

                    if (bindings != null && bindings.hasNext()) {
                        saveCount += deliveryService.saveDeliveries(bindings, savedCountriesByObligationId);
                    }
                }

                log("Going to commit the " + saveCount + " T_DELIVERY rows inserted");
                deliveryService.commitDeliveries(savedCountriesByObligationId);
                log("All inserted T_DELIVERY rows committed succesfully!");
            } else {
                log("CR sparql query call returned 0 deliveries");
            }

            log("Extracting deliveries from CR finished!");
        } catch (Exception e) {
        	deliveryService.rollBackDeliveries();
            log("Error harvesting deliveries: " + e.getMessage());

            log("Operation failed while filling the database from Content Registry. The following error was reported:\n"
                    + e.getMessage());
            LOGGER.error(e.getMessage());
            exitApp(false); // return;
            throw new ResourceNotFoundException("Error getting data from Content Registry " + e.getMessage());
        } 
    }

    /**
     * Get the role from the directory service and save it to database.
     *
     * @param roleName
     * @throws ServiceException
     */
    public void saveRole(String roleName) {

        if (roleName == null || roleName.trim().isEmpty()) {
            return;
        }

        Hashtable<String, Object> role = null;
        try {
            role = DirectoryService.getRole(roleName);
            log("Received role info for " + roleName + " from Directory");
        } catch (DirServiceException de) {
            LOGGER.error("Error getting role " + roleName + ": " + de, de);
            //RODServices.sendEmail("Error in Extractor", "Error getting role " + roleName + ": " + de.toString());
            throw new ResourceNotFoundException("Error getting role " + de, de);
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            //RODServices.sendEmail("Error in Extractor", e.toString());
            throw new ResourceNotFoundException("Error getting role " + e, e);
        }

        if (role == null) {
            return;
        }

        roleService.saveRole(role);
    }
    	
}
