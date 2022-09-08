package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.Servlet;
import org.apache.catalina.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start(Servlet servlet) {
        final var container = new Container(servlet);
        container.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            container.stop();
        }
    }
}
