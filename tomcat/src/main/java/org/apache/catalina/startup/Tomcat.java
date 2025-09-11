package org.apache.catalina.startup;

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.RequestMapping;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Tomcat {

    private static final Logger log = LoggerFactory.getLogger(Tomcat.class);
    private static final int MAX_THREADS_COUNT = 10;
    private static final int DEFAULT_CORE_THREAD_COUNT = 10;

    public void start() {
        SessionManager sessionManager = new SessionManager();
        var connector = new Connector(new RequestMapping(sessionManager), createThreadPool());
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

    private ExecutorService createThreadPool() {
        return new ThreadPoolExecutor(
                DEFAULT_CORE_THREAD_COUNT,
                MAX_THREADS_COUNT,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(10)
        );
    }
}
