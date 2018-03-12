package eionet.rod.service;

import java.io.IOException;
import java.util.Properties;

import eionet.rod.util.exception.ServiceException;


public interface FileServiceIF {
	
	/**
     * DB connection URL for DB connection.
     */
    static final String DB_URL = "db.url";

    /**
     * DB driver.
     */
    static final String DB_DRV = "db.drv";

    /**
     * DB user id for the extractor.
     */
    static final String DB_USER_ID = "db.usr";

    /**
     * User PWD for DB connection.
     */
    static final String DB_USER_PWD = "db.pwd";

    /**
     * DB connection URL for DB connection.
     */
    static final String DB_TEST_URL = "db.unitest.url";

    /**
     * DB driver.
     */
    static final String DB_TEST_DRV = "db.unitest.drv";

    /**
     * DB user id for the extractor.
     */
    static final String DB_TEST_USER_ID = "db.unitest.usr";

    /**
     * User PWD for DB connection.
     */
    static final String DB_TEST_USER_PWD = "db.unitest.pwd";

    /**
     * RO namespace.
     */
    static final String RO_NAMESPACE = "rod.url.ro_namespace";

    /**
     * Namespace for RA titles.
     */
    static final String RA_NAMESPACE = "ra.namespace";

    /**
     * Namespace for countries.
     */
    static final String COUNTRY_NAMESPACE = "country.namespace";

    /**
     * Namespace for T_SPATIAL.
     */
    static final String SPATIAL_NAMESPACE = "spatial.namespace";

    /**
     * Namespace for T_ISSUE.
     */
    static final String ISSUE_NAMESPACE = "issue.namespace";

    /**
     * Namespace for T_CLIENT.
     */
    static final String ORGANISATION_NAMESPACE = "organisation.namespace";

    /**
     * Namespace for Delivery.
     */
    static final String DELIVERY_NAMESPACE = "delivery.namespace";

    /**
     * List of delivery namespaces
     */
    static final String DELIVERY_NAMESPACES = "delivery.namespaces";

    /**
     * namespace separator used for separating namespaces in DELIVERY_NAMESPACES.
     */
    static final String NAMESPACE_SEPARATOR = "namespace.separator";

    /**
     * Content Registry SPARQL endpoint.
     */
    static final String CR_SPARQL_ENDPOINT = "cr.sparql.endpoint";

    /**
     * Title Predicate for rdf type.
     */
    static final String PRED_RDF_TYPE = "pred.rdf-type";

    /**
     * Percent of reporting frequence for approaching deadlines.
     */
    static final String PERCENT_OF_FREQ = "percent.of.freq";

    /**
     * Datefile for DeadlinesDaemon.
     */
    static final String DEADLINES_DAEMON_DATEFILE = "deadlines.daemon.datefile";

    /**
     * DPSIR values Excel file.
     */
    static final String DPSIR_VALUES_FILE = "dpsir.values.file";

    /**
     * XML-RPC server URL for UNS subscription.
     */
    static final String UNS_XMLRPC_SERVER_URL = "uns.xml.rpc.server.url";

    /**
     * Channel name for UNS subscription method.
     */
    static final String UNS_CHANNEL_NAME = "uns.channel.name";

    /**
     * Event type predicate for UNS subscription method.
     */
    static final String UNS_EVENTTYPE_PREDICATE = "uns.eventtype.predicate";

    /**
     * Country predicate for UNS subscription method.
     */
    static final String UNS_COUNTRY_PREDICATE = "uns.country.predicate";

    /**
     * Issue predicate for UNS subscription method.
     */
    static final String UNS_ISSUE_PREDICATE = "uns.issue.predicate";

    /**
     * Obligation predicate for UNS subscription method.
     */
    static final String UNS_OBLIGATION_PREDICATE = "uns.obligation.predicate";

    /**
     * Instrument predicate for UNS subscription method.
     */
    static final String UNS_INSTRUMENT_PREDICATE = "uns.instrument.predicate";

    /**
     * Organisation predicate for UNS subscription method.
     */
    static final String UNS_ORGANISATION_PREDICATE = "uns.organisation.predicate";

    /**
     * UNS subscriptions URL.
     */
    static final String UNS_MY_SUBSCRIPTIONS_URL = "uns.my.subscriptions.url";

    /**
     * username for UNS subscriptions.
     */
    static final String UNS_USERNAME = "uns.username";

    /**
     * password for UNS subscriptions.
     */
    static final String UNS_PWD = "uns.pwd";

    /**
     * UNS xmlrpc function that is called for making subscription.
     */
    static final String UNS_MAKE_SUBSCRIPTION = "uns.make.subsription.function";

    /**
     * UNS xmlrpc function that is called for sending notifications.
     */
    static final String UNS_SEND_NOTIFICATION = "uns.send.notification.function";

    /**
     * Undo - number of records per page.
     */
    static final String UNDO_STEP = "undo.step";

    /**
     * Undo - number of records per page.
     */
    static final String HISTORY_UNDO_STEP = "history.undo.step";


    /**
     * mail for sysadmins.
     */
    static final String MAIL_SYSADMINS = "mail.sysadmins";

    /**
     * flag showing if smtp auth needed.
     */
    static final String MAIL_SMTP_AUTH = "mail.smtp.auth";

    /**
     * mail host.
     */
    static final String MAIL_HOST = "mail.host";

    /**
     * username if auth is switched on.
     */
    static final String MAIL_USER = "mail.user";

    /**
     * mail password.
     */
    static final String MAIL_PASSWORD = "mail.password";

    /**
     * Returns String type property from the properties file.
     *
     * @param propName
     * @return String
     * @throws ServiceException
     */
    String getStringProperty(String propName) throws ServiceException;

    /**
     * Returns String type property. If no property returns default value.
     *
     * @param propName     property name
     * @param defaultValue default value if no property
     * @return property value
     * @throws ServiceException if reading file does not succeed
     */
    String getStringProperty(String propName, String defaultValue) throws ServiceException;


    /**
     * Returns boolean type property from the properties file.
     *
     * @param propName
     * @return boolean
     * @throws ServiceException
     */
    boolean getBooleanProperty(String propName) throws ServiceException;

    /**
     * Returns int type property from the properties file.
     *
     * @param propName
     * @return int
     * @throws ServiceException
     */
    int getIntProperty(String propName) throws ServiceException;

    /**
     * Returns String arrray property from the properties file.
     *
     * @param propName
     * @param separator
     * @return String[]
     * @throws ServiceException
     */
    String[] getStringArrayProperty(String propName, String separator) throws ServiceException;

    /**
     * returns long value of property.
     *
     * @param propName     property name
     * @param defaultValue default value for case if property not specified
     * @return property value
     * @throws ServiceException if reading fails
     */
    long getLongProperty(String propName, long defaultValue) throws ServiceException;

    /**
     * Get property value of time in milliseconds presented by time value and unit suffix (1h, 30m, 15s etc).
     *
     * @param key          property name
     * @param defaultValue default value to be returned if file does not contain the property
     * @return propertyValue
     */
    Long getTimePropertyMilliseconds(final String key, Long defaultValue);


    /**
     * All properties.
     *
     * @return
     */
    Properties getProps() throws IOException;

}
