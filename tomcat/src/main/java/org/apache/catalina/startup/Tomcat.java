package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.Session;
import org.apache.coyote.http11.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.function.Function;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Function<Session, RequestMapping> requestMappingFactory;

    public Tomcat(Function<Session, RequestMapping> requestMappingFactory) {
        this.requestMappingFactory = requestMappingFactory;
    }

    public void start() {
        var connector = new Connector(requestMappingFactory);
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
