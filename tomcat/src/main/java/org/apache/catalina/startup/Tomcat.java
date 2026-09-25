package org.apache.catalina.startup;

import org.apache.catalina.Dispatcher;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.ControllerMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final ControllerMapping controllerMapping;

    public Tomcat(final ControllerMapping controllerMapping) {
        this.controllerMapping = controllerMapping;
    }

    public void start() {
        final RequestHandler requestHandler = new Dispatcher(controllerMapping, new SessionManager());

        final Connector connector = Connector.of(requestHandler);
        connector.startListening();

        registerShutdownHook(connector);
    }

    private void registerShutdownHook(Connector connector) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("web server stop.");
            connector.stopListening();
        }));
    }
}
