package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.HttpHandler;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private HttpHandler handler = (request, response) -> response.setStatus(HttpStatus.NOT_FOUND);

    public void addHandler(final HttpHandler handler) {
        this.handler = handler;
    }

    public void start() {
        var connector = new Connector(handler);
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
