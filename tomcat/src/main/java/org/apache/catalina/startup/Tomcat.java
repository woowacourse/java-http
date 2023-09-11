package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.ExceptionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private final RequestMapping requestMapping;
    private final ExceptionHandler exceptionHandler;

    public Tomcat(RequestMapping requestMapping, ExceptionHandler exceptionHandler) {
        this.requestMapping = requestMapping;
        this.exceptionHandler = exceptionHandler;
    }

    public void start() {
        var connector = new Connector(requestMapping, exceptionHandler);
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
