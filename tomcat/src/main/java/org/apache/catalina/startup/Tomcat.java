package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.config.ControllerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        ControllerMapping.initialize();
        var connector = new Connector(250);
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
