package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.adaptor.ControllerMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final int DEFAULT_MAX_THREADS = 200;
    private final ControllerMapping controllerMapping = new ControllerMapping();

    public void start() {
        var connector = new Connector(controllerMapping, DEFAULT_MAX_THREADS);
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
