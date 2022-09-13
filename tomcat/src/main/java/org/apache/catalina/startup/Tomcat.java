package org.apache.catalina.startup;

import java.io.IOException;
import java.util.List;
import org.apache.catalina.Container;
import org.apache.catalina.servlet.Controller;
import org.apache.catalina.servlet.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start(List<Controller> controllers) {
        final var container = new Container(RequestMapping.of(controllers));
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
