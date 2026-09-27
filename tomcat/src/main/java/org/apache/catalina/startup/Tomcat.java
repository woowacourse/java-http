package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final RequestMapping mapping;

    public Tomcat(RequestMapping mapping) {
        this.mapping = mapping;
    }

    public void start() {
        var connector = new Connector(mapping);
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
