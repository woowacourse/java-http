package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final Connector connector;

    public Tomcat(final Connector connector) {
        this.connector = connector;
    }

    public void start() {
        connector.start();
        Runtime.getRuntime()
                .addShutdownHook(new Thread(() -> {
                    log.info("Web server stopping...");
                    connector.stop();
                }));
    }
}
