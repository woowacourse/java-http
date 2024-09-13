package org.apache.catalina.startup;

import java.awt.Container;
import java.io.IOException;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    public static final int DEFAULT_PORT = 8080;
    public static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_THREAD_POOL_SIZE = 5;
    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        final var connector = new Connector(new Container(), DEFAULT_PORT, DEFAULT_ACCEPT_COUNT,
                DEFAULT_THREAD_POOL_SIZE);
        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }
}
