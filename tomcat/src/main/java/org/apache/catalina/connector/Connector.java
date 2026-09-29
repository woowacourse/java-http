package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.ProcessorFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_QUEUED_REQUESTS = 100;

    private final ExecutorService executorService;
    private final ServerSocket serverSocket;
    private final ProcessorFactory processorFactory;

    private volatile boolean stopped;

    public Connector(final ProcessorFactory processorFactory) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, DEFAULT_MAX_QUEUED_REQUESTS, processorFactory);
    }

    public Connector(final int port,
                     final int acceptCount,
                     final int maxThreads,
                     final ProcessorFactory processorFactory) {
        this(port, acceptCount, maxThreads, DEFAULT_MAX_QUEUED_REQUESTS, processorFactory);
    }

    public Connector(final int port,
                     final int acceptCount,
                     final int maxThreads,
                     final int maxQueuedRequests,
                     final ProcessorFactory processorFactory) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads must be greater than 0: " + maxThreads);
        }
        if (maxQueuedRequests <= 0) {
            throw new IllegalArgumentException("maxQueuedRequests must be greater than 0: " + maxQueuedRequests);
        }
        this.executorService = new ThreadPoolExecutor(
                maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(maxQueuedRequests));
        this.serverSocket = createServerSocket(port, acceptCount);
        this.processorFactory = Objects.requireNonNull(processorFactory);

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

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final Runnable processor = processorFactory.create(connection);
        try {
            executorService.execute(processor);
        } catch (RejectedExecutionException e) {
            log.warn("쓰레드 풀이 꽉차서 커넥션 못함요.", e);
            closeRejectedConnection(connection);
        }
    }

    private void closeRejectedConnection(final Socket connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.warn("거절된 커넥션 닫는데 실패함요.", e);
        }
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            executorService.shutdown();
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
