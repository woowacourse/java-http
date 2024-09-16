package org.apache.catalina.startup;

import jakarta.http.HttpSessionWrapper;
import org.apache.catalina.Manager;
import org.apache.catalina.SessionWrapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.container.Container;
import org.apache.catalina.container.RequestMapping;
import org.apache.catalina.session.SimpleSessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start(RequestMapping requestMapping) {
        Manager manager = new SimpleSessionManager();
        HttpSessionWrapper httpSessionWrapper = new SessionWrapper(manager);
        Container container = new Container(requestMapping, httpSessionWrapper);
        Connector connector = new Connector(container);
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
