package org.apache.catalina.startup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.Container;
import org.apache.coyote.context.HelloWorldContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final String DEFAULT_STATIC_RESOURCE_PATH_PREFIX = "static/";

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final List<Container> containers = new ArrayList<>();

    public void start() {
        Connector connector = new Connector(containers);
        connector.start();

        try {
            System.in.read();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            log.info("web server stop.");
            connector.stop();
        }
    }

    public Container addContainer(final String rootContextPath) {
        return addContainer(rootContextPath, DEFAULT_STATIC_RESOURCE_PATH_PREFIX);
    }

    public Container addContainer(final String rootContextPath, final String staticResourcePath) {
        final HelloWorldContext context = new HelloWorldContext(rootContextPath, staticResourcePath);

        this.containers.add(context);

        return context;
    }
}
