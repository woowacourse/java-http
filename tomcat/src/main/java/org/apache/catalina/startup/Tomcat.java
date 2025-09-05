package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.Context;
import org.apache.catalina.ServletContainer;
import org.apache.catalina.connector.Connector;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private ServletContainer container;
    private Connector connector;

    public void start() {
        final Context context = new Context();
        container = context.createServletContainer();

        connector = new Connector(container);
        connector.start();

        try {
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            stop();
        }
    }

    private void stop() {
        if (connector != null) {
            connector.stop();
        }
        if (container != null) {
            container.destroy();
        }
    }
}
