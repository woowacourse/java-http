package org.apache.catalina.startup;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Container container;

    private Tomcat(final Container container) {
        this.container = container;
    }

    public static class TomcatBuilder {

        private final Map<String, Controller> controllers;

        public TomcatBuilder() {
            this.controllers = new HashMap<>();
        }

        public TomcatBuilder addController(final String path, final Controller controller) {
            this.controllers.put(path, controller);
            return this;
        }

        public Tomcat build() {
            return new Tomcat(Container.from(controllers));
        }
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
