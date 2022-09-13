package org.apache.catalina.startup;

import java.io.IOException;
import nextstep.Application;
import org.apache.catalina.connector.Connector;
import org.apache.coyote.http11.request.mapping.controllerscan.ControllerScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final int DEFAULT_POOL_SIZE = 250;
    private static final int DEFAULT_CORE_SIZE = 50;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 120L;
    private static final int DEFAULT_THREAD_POOL_QUEUE_SIZE = 100;

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);

    public void start() {
        start(
                Connector.DEFAULT_PORT,
                Connector.DEFAULT_ACCEPT_COUNT,
                DEFAULT_POOL_SIZE,
                DEFAULT_CORE_SIZE,
                DEFAULT_KEEP_ALIVE_TIME,
                DEFAULT_THREAD_POOL_QUEUE_SIZE
        );
    }

    public void start(final int port,
                      final int acceptCount,
                      final int maxThreadSize,
                      final int coreThreadSize,
                      final long keepAliveTimeSecond,
                      final int threadPoolQueueSize) {
        ControllerScanner.scan(Application.class.getPackageName());
        var connector = new Connector(
                port,
                acceptCount,
                maxThreadSize,
                coreThreadSize,
                keepAliveTimeSecond,
                threadPoolQueueSize
        );
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
