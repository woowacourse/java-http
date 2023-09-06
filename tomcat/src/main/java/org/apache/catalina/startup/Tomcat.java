package org.apache.catalina.startup;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.Context;
import org.apache.coyote.context.HelloWorldContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final String DEFAULT_STATIC_RESOURCE_PATH_PREFIX = "static/";

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final List<Context> contexts = new ArrayList<>();

    public void start() {
        Connector connector = new Connector(contexts);
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

    public Context addContainer(final String contextPath) {
        return addContainer(contextPath, DEFAULT_STATIC_RESOURCE_PATH_PREFIX);
    }

    public Context addContainer(final String contextPath, final String staticResourcePath) {
        final HelloWorldContext context = new HelloWorldContext(contextPath, staticResourcePath);

        this.contexts.add(context);

        return context;
    }
}
