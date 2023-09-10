package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.handler.FrontController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final FrontController frontController;

    public Tomcat(final FrontController frontController) {
        this.frontController = frontController;
    }

    public void start() {
        final var connector = new Connector(frontController);
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
