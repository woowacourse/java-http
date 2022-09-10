package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.core.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Configuration configuration;

    public Tomcat(final Configuration configuration) {
        this.configuration = configuration;
    }

    public void start() {
        var connector = new Connector(configuration);
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
