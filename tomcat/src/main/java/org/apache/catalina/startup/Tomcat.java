package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.FrontController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {
    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final FrontController frontController;

    public Tomcat() {
        this.frontController = new FrontController();
    }

    public void start() {
        var connector = new Connector(frontController);
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

    public void addController(final String path, final Controller controller) {
        frontController.addController(path, controller);
    }
}
