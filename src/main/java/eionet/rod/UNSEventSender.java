package eionet.rod;


import eionet.rod.model.Subscribe;
import eionet.rod.service.FileServiceIF;
import eionet.rod.util.RODServices;
import org.apache.xmlrpc.XmlRpcClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;

public class UNSEventSender {

    private static final Logger LOGGER = LoggerFactory.getLogger(UNSEventSender.class);

    private UNSEventSender() {
        // Nothing to do here
    }

    /**
     * Utility class for sending the given notifications.
     *
     * @param notifications The notifications to be sent.
     */
    @SuppressWarnings("deprecation")
    public static void makeCall(Vector<Vector<String>> notifications) {
        try {
            FileServiceIF fileSrv = RODServices.getFileService();
            String serverUrl = fileSrv.getStringProperty(FileServiceIF.UNS_XMLRPC_SERVER_URL);
            String channelName = fileSrv.getStringProperty(FileServiceIF.UNS_CHANNEL_NAME);
            if (notifications == null) {
                throw new Exception("Cannot send a null object via XML-RPC");
            }

            XmlRpcClient server = new XmlRpcClient(serverUrl);
            server.setBasicAuthentication(fileSrv.getStringProperty(FileServiceIF.UNS_USERNAME), fileSrv.getStringProperty(FileServiceIF.UNS_PWD));

            Vector<Vector<String>> sanitizedNotifications = sanitizeNotifications(notifications);
            Vector<Object> params = new Vector<>();
            params.add(channelName);
            params.add(sanitizedNotifications);

            String remoteMethodName = fileSrv.getStringProperty(FileServiceIF.UNS_SEND_NOTIFICATION);
            server.execute(remoteMethodName, params);

        } catch (Exception e) {
            LOGGER.error("Failed to send notification", e);
        }
    }

    /**
     * Sanitizes the given notifications before they should be sent.
     *
     * @param notifications The given notifications.
     * @return Sanitized notifications.
     */
    private static Vector<Vector<String>> sanitizeNotifications(Vector<Vector<String>> notifications) {

        if (notifications == null) {
            return new Vector<>();
        }

        // Now vector for the sanitized notifications.
        Vector<Vector<String>> result = new Vector<>();

        // Loop through the vector of notifications.
        // Each notification is a vector too, containing 3 elements, representing an RDF triple (i.e. subject, predicate, object).
        for (Vector<String> notification : notifications) {

            if (notification == null) {
                // Lets replace a null notification with an empty vector, to avoid null-pointer exceptions.
                result.add(new Vector<>());
            } else {
                Vector<String> sanitizedNotification = new Vector<>();
                for (String subjectOrPredicateOrObject : notification) {
                    // If a subject/predicate/object is null, replace it with empty string.
                    sanitizedNotification.add(subjectOrPredicateOrObject == null ? "" : subjectOrPredicateOrObject);
                }
                result.add(sanitizedNotification);
            }
        }

        return result;

    }

    @SuppressWarnings("deprecation")
    public static void subscribe(String userName, Subscribe subscriptions) throws Exception {
        try {
            FileServiceIF fileSrv = RODServices.getFileService();

            String predEventType = fileSrv.getStringProperty(FileServiceIF.UNS_EVENTTYPE_PREDICATE);
            String server_url = fileSrv.getStringProperty(FileServiceIF.UNS_XMLRPC_SERVER_URL);
            String CHANNEL_NAME = fileSrv.getStringProperty(FileServiceIF.UNS_CHANNEL_NAME);

            Vector<Hashtable<String, String>> filters = new Vector<Hashtable<String, String>>();
            Hashtable<String, String> filter = new Hashtable<String, String>();
            Vector<String> eventType = subscriptions.getEventTypes();

            if (eventType != null) {
                for (Iterator<String> it = eventType.iterator(); it.hasNext(); ) {
                    String event = it.next();
                    filter = new Hashtable<String, String>();
                    if (event != null && event.length() > 0)
                        filter.put(predEventType, event);

                    String predCountry = fileSrv.getStringProperty(FileServiceIF.UNS_COUNTRY_PREDICATE);
                    if (subscriptions.getSelectedCountry() != null && subscriptions.getSelectedCountry().length() > 0)
                        filter.put(predCountry, subscriptions.getSelectedCountry());

                    String predIssue = fileSrv.getStringProperty(FileServiceIF.UNS_ISSUE_PREDICATE);
                    if (subscriptions.getSelectedIssue() != null && subscriptions.getSelectedIssue().length() > 0)
                        filter.put(predIssue, subscriptions.getSelectedIssue());

                    String predOrganisation = fileSrv.getStringProperty(FileServiceIF.UNS_ORGANISATION_PREDICATE);
                    if (subscriptions.getSelectedClient() != null && subscriptions.getSelectedClient().length() > 0)
                        filter.put(predOrganisation, subscriptions.getSelectedClient());

                    String predObligation = fileSrv.getStringProperty(FileServiceIF.UNS_OBLIGATION_PREDICATE);
                    if (subscriptions.getSelectedObligation() != null && subscriptions.getSelectedObligation().length() > 0)
                        filter.put(predObligation, subscriptions.getSelectedObligation());

                    String predInstrument = fileSrv.getStringProperty(FileServiceIF.UNS_INSTRUMENT_PREDICATE);
                    if (subscriptions.getSelectedInstrument() != null && subscriptions.getSelectedInstrument().length() > 0)
                        filter.put(predInstrument, subscriptions.getSelectedInstrument());

                    if (filter.size() > 0)
                        filters.add(filter);
                }
            }

            if (filters.size() > 0) {
                XmlRpcClient server = new XmlRpcClient(server_url);
                server.setBasicAuthentication(fileSrv.getStringProperty(FileServiceIF.UNS_USERNAME),
                        fileSrv.getStringProperty(FileServiceIF.UNS_PWD));
                // make subscription
                Vector<Object> params = new Vector<Object>();
                params.add(CHANNEL_NAME);
                params.add(userName);
                params.add(filters);
                server.execute(fileSrv.getStringProperty(FileServiceIF.UNS_MAKE_SUBSCRIPTION), params);
            }
        } catch (Exception e){
            LOGGER.warn("Problem subscribing: " + e.getMessage(), e);
            throw e;
        }
    }

}
