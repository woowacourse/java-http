package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int ACCEPT_COUNT = 100;
    private static final int MAX_THREADS = 250;

    public void start() {
        var connector = new Connector(DEFAULT_PORT, ACCEPT_COUNT, MAX_THREADS);
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
