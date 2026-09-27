package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Consumer;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Consumer<Http11Processor> connectionHandler;

    public Tomcat(Consumer<Http11Processor> connectionHandler) {
        this.connectionHandler = connectionHandler;
    }

    public void start() {
        var connector = new Connector(connectionHandler);
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
