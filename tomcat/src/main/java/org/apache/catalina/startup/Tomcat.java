package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.Mapper;
import org.apache.coyote.http11.controller.Controller;
import org.apache.coyote.http11.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Mapper mapper = new RequestMapping();

    public Tomcat addController(String path, Controller controller) {
        mapper.addController(path, controller);
        return this;
    }

    public void start() {
        final Connector connector = new Connector(mapper);
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
