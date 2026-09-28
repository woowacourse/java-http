package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.controller.Controller;
import org.apache.coyote.controller.RequestMapping;
import org.apache.coyote.http11.Http11Processor;

import org.apache.coyote.http11.session.HttpSessionHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final long TERMINATION_TIMEOUT_SECONDS = 10;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private final Semaphore connectionLimit;
    private volatile boolean stopped;
    private final RequestMapping requestMapping;
    private final Controller staticResourceController;
    private final HttpSessionHandler sessionHandler;

    public Connector(
            final RequestMapping requestMapping,
            final Controller staticResourceController,
            final HttpSessionHandler sessionHandler
    ) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS,
                requestMapping, staticResourceController, sessionHandler);
    }

    public Connector(
            final int port,
            final int acceptCount,
            final int maxThreads,
            final RequestMapping requestMapping,
            final Controller staticResourceController,
            final HttpSessionHandler sessionHandler
    ) {
        this.serverSocket = createServerSocket(port, acceptCount);
        final int checkedMaxThreads = checkMaxThreads(maxThreads);
        this.executorService = Executors.newFixedThreadPool(checkedMaxThreads);
        this.connectionLimit = new Semaphore(checkedMaxThreads);
        this.requestMapping = requestMapping;
        this.staticResourceController = staticResourceController;
        this.sessionHandler = sessionHandler;
        this.stopped = false;
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

    public void start() {
        var thread = new Thread(this);
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
        // 모든 스레드가 사용 중이면 accept()를 멈춰 대기 요청이 OS backlog(acceptCount)에 쌓이게 한다.
        try {
            connectionLimit.acquire();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            stopped = true;
            return;
        }
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            connectionLimit.release();
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        final Http11Processor processor =
                new Http11Processor(connection, requestMapping, staticResourceController, sessionHandler);
        try {
            executorService.execute(() -> {
                try {
                    processor.run();
                } finally {
                    connectionLimit.release();
                }
            });
        } catch (RejectedExecutionException e) {
            connectionLimit.release();
            closeQuietly(connection);
            log.warn("Connection rejected: {}", e.getMessage());
        }
    }

    private void closeQuietly(final Socket connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        shutdownExecutor();
    }

    private void shutdownExecutor() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
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
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }

    private int checkMaxThreads(final int maxThreads) {
        if (maxThreads < 1) {
            return DEFAULT_MAX_THREADS;
        }
        return maxThreads;
    }
}
