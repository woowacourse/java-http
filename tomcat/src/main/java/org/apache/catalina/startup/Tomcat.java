package org.apache.catalina.startup;

import com.techcourse.api.RequestMapping;
import com.techcourse.api.RequestMappingFactory;
import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.catalina.connector.CatalinaHttpHandler;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.HttpHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        final Manager sessionManager = SessionManager.getInstance();
        final RequestMapping requestMapping = RequestMappingFactory.create(sessionManager);
        final HttpHandler httpHandler = new CatalinaHttpHandler(sessionManager, requestMapping);

        final Connector connector = new Connector(httpHandler);
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }
}
