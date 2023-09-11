package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.servletcontainer.Handler;
import org.apache.catalina.servletcontainer.HandlerContainer;
import org.apache.coyote.http11.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Container container = new HandlerContainer();

    public void start() {
        var connector = new Connector(container);
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

    public void addHandler(String url, Handler handler) {
        container.addHandler(url, handler);
    }
}
