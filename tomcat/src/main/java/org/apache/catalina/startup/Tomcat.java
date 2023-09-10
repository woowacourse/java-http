package org.apache.catalina.startup;

import java.io.IOException;
import java.util.Set;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.servlet.RequestHandlerAdaptor;
import org.apache.catalina.servlet.handler.Servlet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start(final Set<Servlet> requestHandlers) {
        final var connector = new Connector(new RequestHandlerAdaptor(requestHandlers));
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
