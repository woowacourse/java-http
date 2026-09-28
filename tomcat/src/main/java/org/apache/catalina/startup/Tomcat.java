package org.apache.catalina.startup;

import java.io.IOException;
import java.util.Map;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.ControllerResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final ControllerResolver controllerResolver;

    public Tomcat(
            Map<String, Controller> controllers,
            Controller defaultController
    ) {
        this.controllerResolver = new ControllerResolver(
                controllers,
                defaultController
        );
    }

    public void start() {
        var connector = new Connector(controllerResolver);
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
