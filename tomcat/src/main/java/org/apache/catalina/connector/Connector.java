package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int MAX_WAITING_REQUESTS = 100;
    private static final int SOCKET_READ_TIMEOUT_MILLIS = 5_000;
    private static final int SHUTDOWN_TIMEOUT_MILLIS = 5_000;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private volatile boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads must be greater than 0");
        }
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executorService = new ThreadPoolExecutor(
                maxThreads, maxThreads,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(MAX_WAITING_REQUESTS),
                new ThreadPoolExecutor.AbortPolicy()
        );
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
        stopped = false;
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

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            if (!stopped) {
                log.error(e.getMessage(), e);
            }
        }
    }

    void process(final Socket connection) {
        if (connection == null) {
            return;
        }

        try {
            connection.setSoTimeout(SOCKET_READ_TIMEOUT_MILLIS);
            executorService.execute(new Http11Processor(connection));
        } catch (IOException e) {
            log.error("연결을 설정하는 중 오류가 발생했습니다.", e);
            closeConnection(connection);
        } catch (RejectedExecutionException e) {
            closeConnection(connection);
        }
    }

    private void closeConnection(final Socket connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.error("연결을 닫는 중 오류가 발생했습니다.", e);
        }
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                log.warn("Request processing did not finish within {} ms.", SHUTDOWN_TIMEOUT_MILLIS);
            }
        } catch (InterruptedException e) {
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
}
