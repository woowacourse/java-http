package org.apache.catalina.startup;

import org.apache.catalina.connector.Connector;
import org.apache.coyote.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    private final RequestMapping requestMapping;
    private final int maxThreads;
    private final int waitingQueueSize;

    public Tomcat(
            final RequestMapping requestMapping,
            final int maxThreads,
            final int waitingQueueSize
    ) {
        this.requestMapping = requestMapping;
        this.maxThreads = maxThreads;
        this.waitingQueueSize = waitingQueueSize;
    }

    public void start() {
        var connector = new Connector(requestMapping, maxThreads, waitingQueueSize);
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
