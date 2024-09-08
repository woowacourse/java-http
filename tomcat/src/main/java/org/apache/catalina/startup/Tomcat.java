package org.apache.catalina.startup;

import org.apache.catalina.Manager;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.session.SimpleSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Manager manager = new SimpleSessionManager();

    public void start() {
        var connector = new Connector(manager);
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
