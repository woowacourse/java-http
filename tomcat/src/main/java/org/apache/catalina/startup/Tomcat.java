package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;

    private final int maxThreads;

    public Tomcat() {
        this(DEFAULT_MAX_THREADS);
    }

    public Tomcat(final int maxThreads) {
        this.maxThreads = maxThreads;
    }

    public void start() {
        var connector = new Connector(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, maxThreads);
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
