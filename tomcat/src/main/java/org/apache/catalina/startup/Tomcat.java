package org.apache.catalina.startup;

import java.io.IOException;
import java.util.Map;
import org.apache.catalina.RequestAdapter;
import org.apache.catalina.RequestMapper;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.http11.Adapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final int DEFAULT_THREAD_COUNT = 250;

    private final Adapter adapter;

    public Tomcat(final Map<String, Controller> controllers) {
        final RequestMapper requestMapper = new RequestMapper(controllers);
        this.adapter = new RequestAdapter(requestMapper);
    }

    public void start() {
        final Connector connector = new Connector(adapter, DEFAULT_THREAD_COUNT);

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
