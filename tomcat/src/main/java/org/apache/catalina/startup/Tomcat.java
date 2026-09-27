package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.mapper.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final RequestMapping requestMapping;
    private final Manager sessionManager;

    public Tomcat(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
        this.sessionManager = SessionManager.getInstance();
    }

    public void start() {
        var connector = new Connector(requestMapping, sessionManager);
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
