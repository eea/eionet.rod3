package eionet.rod.util.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception class for service layer error/exception situations.
 *
 * @author Rando Valt
 * @version 1.0
 */
public class ServiceException extends java.lang.Exception {
	
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
