package eionet.rod.service.modules;

import java.io.IOException;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

import eionet.rod.service.FileServiceIF;
import eionet.rod.util.exception.ServiceException;

public class FileServiceImpl implements FileServiceIF  {
	
	private final ResourceBundle props;
	
	public static final String PROP_FILE = "application";

	/**
     * Creates new FileServiceImpl
     */
    public FileServiceImpl() throws ServiceException {
        try {
            props = ResourceBundle.getBundle(PROP_FILE);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Properties file " + PROP_FILE + ".properties not found");
        }

    }

    /**
     *
     */
    public String getStringProperty(String propName) throws ServiceException {
        try {
        	System.out.print(propName);
            return props.getString(propName);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Property value for key " + propName + " not found");
        }
    }

    /**
     *
     */
    public boolean getBooleanProperty(String propName) throws ServiceException {
        try {
            String s = props.getString(propName);
            return Boolean.valueOf(s);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Property value for key " + propName + " not found");
        }
    }

    /**
     *
     */
    public int getIntProperty(String propName) throws ServiceException {
        try {
            String s = props.getString(propName);
            return Integer.parseInt(s);
        } catch (MissingResourceException mre) {
            throw new ServiceException("Property value for key " + propName + " not found");
        } catch (NumberFormatException nfe) {
            throw new ServiceException("Invalid value for integer property " + propName);
        }
    }

    @Override
    public long getLongProperty(String propName, long defaultValue) throws ServiceException {
        String value;
        if (props.containsKey(propName)) {
            value = props.getString(propName);
            return Long.parseLong(value);

        }
        return defaultValue;

    }

    /**
     *
     */
    public String[] getStringArrayProperty(String propName, String separator) throws ServiceException {
        try {
            String[] str = null;
            String s = props.getString(propName);

            if (separator == null || separator.isEmpty()) {
                str = new String[1];
                str[0] = s;
            } else {
                char c = separator.charAt(0);
                String sep = Character.isLetterOrDigit(c) ? Character.toString(c) : "\\" + c;
                str = s.split(sep);
            }
            return str;
        } catch (MissingResourceException mre) {
            throw new ServiceException("Property value for key " + propName + " not found (" + mre.getMessage() + ")");
        }
    }

    @Override
    public synchronized Long getTimePropertyMilliseconds(final String key, Long defaultValue) {

        Long value = defaultValue;

        if (props.containsKey(key)) {
            String propValue = props.getString(key);

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
                value = Long.parseLong(propValue) * coefficient;
            } catch (Exception e) {
                // Ignore exceptions resulting from string-to-integer conversion here.
            }
        }

        return value;
    }

    @Override
    public String getStringProperty(String propName, String defaultValue) throws ServiceException {
        if (props.containsKey(propName)) {
            return  getStringProperty(propName);
        }

        return defaultValue;
    }
    
    @Override
    public Properties getProps() throws IOException{
        Properties props = new Properties();
        
        props.load(getClass().getClassLoader().getResourceAsStream(PROP_FILE + ".properties"));
        
        
        return props;
        
        
    }

}
