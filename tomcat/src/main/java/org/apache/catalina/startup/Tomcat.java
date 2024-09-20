package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {
    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final int port;
    private final int acceptAccount;
    private final int maxThreads;


    public Tomcat(int port, int acceptAccount, int maxThreads) {
        this.port = port;
        this.acceptAccount = acceptAccount;
        this.maxThreads = maxThreads;

    }

    public void start() {
        var connector = new Connector(port, acceptAccount, maxThreads);
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
