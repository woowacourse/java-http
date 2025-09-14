package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final ControllerContainer controllerContainer;

    public Tomcat() {
        this.controllerContainer = new ControllerContainer();
    }

    public void addController(String path, Controller controller) {
        controllerContainer.addControllerWithPath(path, controller);
    }

    public void start() {
        var connector = new Connector(controllerContainer);
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
