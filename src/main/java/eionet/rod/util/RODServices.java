package eionet.rod.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import eionet.rod.service.EmailServiceIF;
import eionet.rod.service.FileServiceIF;

import eionet.rod.service.modules.EmailServiceImpl;
import eionet.rod.service.modules.FileServiceImpl;
import eionet.rod.util.exception.ServiceException;

public class RODServices {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(RODServices.class);
    //private static RODDaoFactory daoFactory = null;
    private static FileServiceIF _fSrv = null;
    private static EmailServiceIF emailService = null;

    /**
     * Instance of FileServiceIF (reads from props file).
     * @return FileServiceIF
     * @throws ServiceException 
     */
    public static FileServiceIF getFileService() throws ServiceException {
        if (_fSrv == null)
            _fSrv = new FileServiceImpl();

        return _fSrv; // new FileServiceImpl();
    }

    /**
     * new instance of emailservice.
     * @return
     * @throws ServiceException
     */
    public static EmailServiceIF getEmailService()  {
        if (emailService == null) {
            emailService = new EmailServiceImpl();
        }

        return emailService;
    }
    
  
    /**
     * send email to sys admins specified in the props file.
     * @param subject
     * @param msg
     */
    public static void sendEmail(String subject, String msg) {
        
        try {
            getEmailService().sendToSysAdmin(subject, msg);
        } catch (Exception e) {
            LOGGER.error("Sending email failed ", e);
        }
        
    }

}
