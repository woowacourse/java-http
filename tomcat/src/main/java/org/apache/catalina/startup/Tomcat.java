package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.ControllerMapper;
import org.apache.catalina.controller.ControllerStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final String SERVER_STOP_MESSAGE = "web server stop.";

    private final ControllerMapper controllerMapper = new ControllerMapper();

    public void start() {
        final var connector = new Connector(controllerMapper);
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info(SERVER_STOP_MESSAGE);
            connector.stop();
        }
    }

    public void addController(final ControllerStatus controllerStatus, final Controller controller) {
        controllerMapper.addController(controllerStatus, controller);
    }
}
