package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.controller.RequestMapper;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final RequestMapper requestMapper;

    public Tomcat(final RequestMapper requestMapper) {
        this.requestMapper = requestMapper;
    }

    public void start() {
        var connector = new Connector(requestMapper);
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
