package org.apache.catalina.startup;

import com.techcourse.servlet.Servlet;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final List<Servlet> servlets;

    public Tomcat(List<Servlet> servlets) {
        this.servlets = servlets;
    }

    public void start() {
        var connector = new Connector(servlets);
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
