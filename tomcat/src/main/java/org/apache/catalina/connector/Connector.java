package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.SimpleSessionManager;
import org.apache.coyote.http11.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;

    private final ServerSocket serverSocket;
    private final SimpleSessionManager sessionManager;
    private final RequestMapping requestMapping;
    private final ExecutorService executorService;
    private volatile boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads는 1 이상이어야 합니다.");
        }
        this.serverSocket = createServerSocket(port, acceptCount);
        this.sessionManager = new SimpleSessionManager();
        this.requestMapping = new RequestMapping(sessionManager);
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.stopped = false;
    }

    public void start() {
        stopped = false;
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect();
        }
    }

    public void stop() {
        stopped = true;
        executorService.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            if (!stopped) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        try {
            executorService.execute(new Http11Processor(connection, sessionManager, requestMapping));
        } catch (RejectedExecutionException e) {
            log.warn("요청을 처리할 수 없어 연결을 닫습니다.", e);
            try {
                connection.close();
            } catch (IOException closeException) {
                log.warn("연결을 닫지 못했습니다.", closeException);
            }
        }
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private int checkPort(final int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(final int acceptCount) {
        return acceptCount > 0 ? acceptCount : DEFAULT_ACCEPT_COUNT;
    }
}
