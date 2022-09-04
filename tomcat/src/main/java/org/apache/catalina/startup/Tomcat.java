package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.servlet.Servlet;
import org.apache.coyote.servlet.session.SessionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final SessionRepository sessionRepository;

    public Tomcat(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public void start(Servlet servlet) {
        final var connector = new Connector(servlet, sessionRepository);
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
