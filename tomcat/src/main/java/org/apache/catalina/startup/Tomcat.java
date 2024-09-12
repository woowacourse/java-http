package org.apache.catalina.startup;

import java.io.IOException;
import org.apache.catalina.route.DefaultDispatcher;
import org.apache.catalina.route.RequestMapper;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final Dispatcher dispatcher;

    public Tomcat() {
        RequestMapper requestMapper = new RequestMapper();
        this.dispatcher = new DefaultDispatcher(requestMapper);
    }

    public Tomcat(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public void start() {
        var connector = new Connector(dispatcher);
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
