package eionet.rod.service.modules;

import javax.mail.PasswordAuthentication;

public class EMailAuthenticator extends javax.mail.Authenticator {

    /**
     *
     */
    private String user;
    /**
     *
     */
    private String password;

    /**
     * @param username username
     * @param password password
     */
    public EMailAuthenticator(String username, String password) {
        this.user = username;
        this.password = password;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PasswordAuthentication getPasswordAuthentication() {

        return new PasswordAuthentication(user, password);
    }

}
