package org.apache.catalina.startup;

import nextstep.jwp.EdenConfig;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.Configuration;
import org.apache.coyote.http11.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final RequestMapping requestMapping;

    public Tomcat(Configuration configuration) {
        this.requestMapping = new RequestMapping();
        configuration.addControllers(requestMapping);
        configuration.setFileController(requestMapping);
    }

    public void start() {
        var connector = new Connector(requestMapping);
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
