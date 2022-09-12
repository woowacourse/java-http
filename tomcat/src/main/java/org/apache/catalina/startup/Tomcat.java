package org.apache.catalina.startup;

import java.util.List;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.controller.Controller;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public Tomcat() {
    }

    public void start(final List<Controller> controllers) {
        var connector = new Connector(controllers);
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
