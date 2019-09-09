package eionet.rod.service.modules;

import eionet.rod.service.FileServiceIF;
import eionet.rod.util.exception.ServiceException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

public class FileServiceImpl implements FileServiceIF {

    private static final Log logger = LogFactory.getLog(FileServiceImpl.class);

    private final ResourceBundle props;

    public static final String PROP_FILE = "application";

    /**
     * Creates new FileServiceImpl
     */
    public FileServiceImpl() throws ServiceException {
        try {
            props = ResourceBundle.getBundle(PROP_FILE);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Properties file " + PROP_FILE + ".properties not found", mre);
        }

    }

    /**
     *
     */
    public String getStringProperty(String propName) throws ServiceException {
        try {
            // dynamic properties first
            if(System.getProperty(propName) != null) {
                return System.getProperty(propName);
            }
            return props.getString(propName);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Property value for key " + propName + " not found", mre);
        }
    }

    /**
     *
     */
    public boolean getBooleanProperty(String propName) throws ServiceException {
        try {
            String s = getStringProperty(propName);
            return Boolean.parseBoolean(s);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Property value for key " + propName + " not found", mre);
        }
    }

    /**
     *
     */
    public int getIntProperty(String propName) throws ServiceException {
        try {
            String s = getStringProperty(propName);
            return Integer.parseInt(s);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Property value for key " + propName + " not found", mre);
        } catch (NumberFormatException nfe) {
            throw new ServiceException("Invalid value for integer property " + propName, nfe);
        }
    }

    @Override
    public long getLongProperty(String propName, long defaultValue) throws ServiceException {
        try {
            return Long.parseLong(getStringProperty(propName));
        } catch (Exception se) {
            // not found OR cannot parse!
            return defaultValue;
        }
    }

    @Override
    public synchronized Long getTimePropertyMilliseconds(final String key, Long defaultValue) {

        try {
            String propValue = getStringProperty(key);

            int coefficient = 1;

            propValue = propValue.replace(" ", "").toLowerCase();

            if (propValue.length() > 1 && propValue.endsWith("ms")
                    && propValue.replace("ms", "").length() == propValue.length() - 2) {
                coefficient = 1;
                propValue = propValue.replace("ms", "");
            }

            if (propValue.length() > 1 && propValue.endsWith("s")
                    && propValue.replace("s", "").length() == propValue.length() - 1) {
                coefficient = 1000;
                propValue = propValue.replace("s", "");
            }

            if (propValue.length() > 1 && propValue.endsWith("m")
                    && propValue.replace("m", "").length() == propValue.length() - 1) {
                coefficient = 1000 * 60;
                propValue = propValue.replace("m", "");
            }

            if (propValue.length() > 1 && propValue.endsWith("h")
                    && propValue.replace("h", "").length() == propValue.length() - 1) {
                coefficient = 1000 * 60 * 60;
                propValue = propValue.replace("h", "");
            }

            try {
                return Long.parseLong(propValue) * coefficient;
            } catch (Exception e) {
                // Ignore exceptions resulting from string-to-integer conversion here.
                logger.debug("Ignored " + e + " for " + propValue, e);
            }
        } catch (ServiceException se) {
            // nothing really
        }
        return defaultValue;
    }

    @Override
    public String getStringProperty(String propName, String defaultValue) throws ServiceException {
        try {
            return getStringProperty(propName);
        } catch (ServiceException se) {
            return defaultValue;
        }
    }

    @Override
    public Properties getProps() throws IOException {
        Properties props = new Properties();

        props.load(getClass().getClassLoader().getResourceAsStream(PROP_FILE + ".properties"));

        return props;
    }

}
