package eionet.rod.service;

import java.io.IOException;

import javax.mail.MessagingException;

import eionet.rod.util.exception.ServiceException;

public interface EmailServiceIF {
	void sendToSysAdmin(String subject, String body) throws MessagingException, ServiceException, IOException;
}
