package eionet.rod.service;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

/**
 * The way the RPC service initializes the service class doesn't allow for Spring autowiring.
 * This is a workaround to access the Spring beans from that service.
 */
@Component
public class SpringContext implements ApplicationContextAware {
    @Autowired
    private static ApplicationContext context;

    public static ApplicationContext getApplicationContext() {
        return context;
    }

    public void setApplicationContext(ApplicationContext context) throws BeansException {
        this.context = context;
    }
}