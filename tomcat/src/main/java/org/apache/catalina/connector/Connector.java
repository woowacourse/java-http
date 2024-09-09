package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.executor.RequestExecutors;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final RequestExecutors requestExecutors;
    private final SessionManager sessionManager;

    private boolean stopped;

    public Connector(final ServerSocket serverSocket, final ExecutorService executorService, final RequestExecutors requestExecutors, final SessionManager sessionManager) {
        this.serverSocket = serverSocket;
        this.stopped = false;
        this.executorService = executorService;
        this.requestExecutors = requestExecutors;
        this.sessionManager = sessionManager;

    }

    public void start() {
        final var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final var processor = new Http11Processor(connection, requestExecutors, sessionManager);
        executorService.submit(processor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
