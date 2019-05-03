package eionet.rod.service;

import eionet.rod.util.exception.ServiceException;

import javax.mail.MessagingException;
import java.io.IOException;

public interface EmailServiceIF {
    void sendToSysAdmin(String subject, String body) throws MessagingException, ServiceException, IOException;
}
