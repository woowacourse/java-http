package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Map;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final RequestMapping requestMapping;

    public Tomcat() {
        this(Map.of());
    }

    public Tomcat(final Map<String, Controller> controllers) {
        this.requestMapping = new RequestMapping(controllers);
    }

    public void start() {
        var connector = new Connector(requestMapping);
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
