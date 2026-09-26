package org.apache.catalina.startup;

import org.apache.catalina.Manager;
import org.apache.catalina.SessionManager;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Controller;
import org.apache.coyote.http11.RegisterController;
import org.apache.coyote.http11.RequestMapping;
import org.apache.coyote.http11.StaticResourceController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        Manager sessionManager = new SessionManager();
        Controller staticResourceController = new StaticResourceController();
        RequestMapping requestMapping = new RequestMapping(staticResourceController);
        requestMapping.register(
                "/register",
                new RegisterController(staticResourceController)
        );

        var connector = new Connector(sessionManager, requestMapping);

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
