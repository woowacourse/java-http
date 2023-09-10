package org.apache.catalina.startup;

import java.util.List;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Container container;

    private Tomcat(final Container container) {
        this.container = container;
    }

    public static Tomcat from(final List<Controller> controllers) {
        return new Tomcat(Container.from(controllers));
    }

    public void start() {
        var connector = new Connector(container);
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
