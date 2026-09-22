package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Objects;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Adapter adapter;

    public Tomcat(Adapter adapter) {
        this.adapter = Objects.requireNonNull(adapter);
    }

    public void start() {
        var connector = new Connector(adapter);
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
