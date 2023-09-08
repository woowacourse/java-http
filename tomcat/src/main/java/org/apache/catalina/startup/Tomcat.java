package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.controller.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final RequestHandler requestHandler;

    public Tomcat(final RequestHandler requestHandler) {
        this.requestHandler = requestHandler;
    }

    public void start() {
        final var connector = new Connector(requestHandler);
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }
}
