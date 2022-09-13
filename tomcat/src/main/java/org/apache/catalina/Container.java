package org.apache.catalina;

import java.io.IOException;
import java.net.Socket;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.executor.Executor;
import org.apache.catalina.servlet.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Container implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Container.class);

    private final RequestMapping requestMapping;
    private final SessionManager sessionManager = new SessionManager();
    private final Connector connector;
    private final Executor executor;
    private boolean stopped;

    public Container(final RequestMapping requestMapping) {
        this.requestMapping = requestMapping;
        this.connector = new Connector();
        this.executor = new Executor();
        this.stopped = false;
    }

    public void start() {
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
    }

    @Override
    public void run() {
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            process(connector.connect());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        executor.execute(new Http11Processor(connection, requestMapping, sessionManager));
    }

    public void stop() {
        stopped = true;
        connector.stop();
    }
}
