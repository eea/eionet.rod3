package eionet.rod;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.support.XmlWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

//import javax.servlet.ServletException;
//import org.jasig.cas.client.session.SingleSignOutHttpSessionListener;
//import org.springframework.web.context.ContextLoaderListener;

/**
 * Initialise DispatcherServlet.
 * <p>
 * See http://docs.spring.io/spring/docs/current/spring-framework-reference/htmlsingle/#mvc-container-config
 */
public class WebAppInitializer implements WebApplicationInitializer {

    private static final Log logger = LogFactory.getLog(WebAppInitializer.class);

    @Override
    public void onStartup(ServletContext container) {
        XmlWebApplicationContext appContext = new XmlWebApplicationContext();
        appContext.setConfigLocation("/WEB-INF/classes/spring-db-config.xml "
                + "/WEB-INF/classes/spring-init-config.xml "
                + "/WEB-INF/classes/spring-mvc-config.xml "
                + "/WEB-INF/classes/spring-service-config.xml");

        ServletRegistration.Dynamic dispatcher = container.addServlet("dispatcher", new DispatcherServlet(appContext));

        String location = null;
        try {
            location = System.getProperty("upload.dir", null);
        } catch (Exception e) {
            location = null;
        }

        logger.info("Upload directory configured to: " + (location == null ? "[default]" : location));

        //long maxFileSize = 5000000L;
        //long maxRequestSize = 5000000L;
        //int fileSizeThreshold = 0;
        MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location);
        //MultipartConfigElement multipartConfigElement = new MultipartConfigElement(location, maxFileSize, maxRequestSize, fileSizeThreshold);
        dispatcher.setMultipartConfig(multipartConfigElement);

        dispatcher.setLoadOnStartup(1);
        dispatcher.addMapping("/");

    }

}
