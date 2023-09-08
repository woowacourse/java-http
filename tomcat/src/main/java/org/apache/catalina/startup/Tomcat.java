package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.RequestMapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.Controller;
import org.apache.catalina.controller.StaticController;
import org.apache.coyote.http11.Mapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Mapper mapper = new RequestMapper(new StaticController());

    public void addController(final String path, final Controller controller) {
        mapper.addController(path, controller);
    }

    public void start() {
        var connector = new Connector(mapper);

        connector.start();

        try {
            // make the application wait until we press any key.
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally{
            log.info("web server stop.");
            connector.stop();
        }
    }
}
