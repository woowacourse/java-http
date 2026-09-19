package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        Connector connector = Connector.create();
        connector.startListening();

        registerShutdownHook(connector);
    }

    private void registerShutdownHook(Connector connector) {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            log.info("web server stop.");
            connector.stopListening();
        }));
    }
}
