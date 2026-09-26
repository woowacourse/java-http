package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.ControllerResolver;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private final ControllerResolver controllerResolver;
    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public Tomcat(ControllerResolver controllerResolver) {
        this.controllerResolver = controllerResolver;
    }

    public void start() {
        var connector = new Connector(
                8080, 100, controllerResolver
        );
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
