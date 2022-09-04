package org.apache.catalina.startup;

import java.io.IOException;
import java.util.List;
import org.apache.catalina.connector.Connector;
import org.apache.config.CompositionRoot;
import org.apache.mvc.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final CompositionRoot ROOT = new CompositionRoot();

    private final List<Controller> controllers;

    public Tomcat(List<Controller> controllers) {
        this.controllers = controllers;
    }

    public void start() {

        Connector connector = new Connector(ROOT.getHandlerChain(controllers));
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
