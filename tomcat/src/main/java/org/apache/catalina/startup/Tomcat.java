package org.apache.catalina.startup;

import org.apache.catalina.ControllerMapping;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final ControllerMapping controllerMapping;

    public Tomcat(ControllerMapping controllerMapping) {
        this.controllerMapping = Objects.requireNonNull(controllerMapping);
    }

    public void start() {
        var connector = new Connector(controllerMapping);
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
