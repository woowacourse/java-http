package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.Adapter;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int DEFAULT_REQUEST_QUEUE_CAPACITY = 100;
    private static final long KEEP_ALIVE_TIME_SECONDS = 60;

    private final ServerSocket serverSocket;
    private final Adapter adapter;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector(final Adapter adapter) {
        this(adapter, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final Adapter adapter) {
        this(adapter, port, acceptCount, DEFAULT_MAX_THREADS);
    }

    public Connector(final Adapter adapter, final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.adapter = adapter;
        final ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                KEEP_ALIVE_TIME_SECONDS,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(DEFAULT_REQUEST_QUEUE_CAPACITY)
        );
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        this.executorService = threadPoolExecutor;
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
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
        final var processor = new Http11Processor(connection, adapter);
        try {
            executorService.execute(processor);
        } catch (final RejectedExecutionException e) {
            log.warn("스레드 풀 또는 대기열이 가득 찼거나 종료 중이어서 연결을 거절했습니다.");
            closeConnection(connection);
        }
    }

    private void closeConnection(final Socket connection) {
        try {
            connection.close();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void stop() {
        stopped = true;
        executorService.shutdown();
        try {
            serverSocket.close();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
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
