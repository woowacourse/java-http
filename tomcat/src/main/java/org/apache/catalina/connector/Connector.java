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
    private static final int DEFAULT_MAX_QUEUE_SIZE = 100;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 5L;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private volatile boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, DEFAULT_MAX_QUEUE_SIZE);
    }

    public Connector(final int port, final int acceptCount) {
        this(port, acceptCount, DEFAULT_MAX_THREADS, DEFAULT_MAX_QUEUE_SIZE);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this(port, acceptCount, maxThreads, DEFAULT_MAX_QUEUE_SIZE);
    }

    public Connector(
            final int port,
            final int acceptCount,
            final int maxThreads,
            final int maxQueueSize
    ) {
        this(
                createServerSocket(port, acceptCount),
                createExecutorService(maxThreads, maxQueueSize)
        );
    }

    Connector(final ServerSocket serverSocket, final ExecutorService executorService) {
        this.serverSocket = serverSocket;
        this.executorService = executorService;
        this.stopped = false;
    }

    private static ExecutorService createExecutorService(final int maxThreads, final int maxQueueSize) {
        final int checkedMaxThreads = checkMaxThreads(maxThreads);
        final int checkedMaxQueueSize = checkMaxQueueSize(maxQueueSize);
        return new ThreadPoolExecutor(
                checkedMaxThreads,
                checkedMaxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(checkedMaxQueueSize)
        );
    }

    private static ServerSocket createServerSocket(final int port, final int acceptCount) {
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
        final var thread = new Thread(this);
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
            if (!stopped) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final var connectionTask = new ConnectionTask(connection);
        try {
            executorService.execute(connectionTask);
        } catch (RejectedExecutionException e) {
            connectionTask.close();
            log.warn("HTTP request rejected because the thread pool is full or stopping", e);
        }
    }

    public void stop() {
        stopped = true;
        closeServerSocket();
        shutdownExecutorService();
    }

    private void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                forceShutdown();
                awaitForcedTermination();
            }
        } catch (InterruptedException e) {
            forceShutdown();
            Thread.currentThread().interrupt();
        }
    }

    private void forceShutdown() {
        executorService.shutdownNow().stream()
                .filter(ConnectionTask.class::isInstance)
                .map(ConnectionTask.class::cast)
                .forEach(ConnectionTask::close);
    }

    private void awaitForcedTermination() throws InterruptedException {
        if (!executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            log.warn("HTTP worker thread pool did not terminate");
        }
    }

    private static int checkPort(final int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private static int checkAcceptCount(final int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }

    private static int checkMaxThreads(final int maxThreads) {
        if (maxThreads <= 0) {
            return DEFAULT_MAX_THREADS;
        }
        return maxThreads;
    }

    private static int checkMaxQueueSize(final int maxQueueSize) {
        if (maxQueueSize <= 0) {
            return DEFAULT_MAX_QUEUE_SIZE;
        }
        return maxQueueSize;
    }

    private void close(final Socket connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.warn("Failed to close rejected connection", e);
        }
    }

    private final class ConnectionTask implements Runnable {

        private final Socket connection;

        private ConnectionTask(final Socket connection) {
            this.connection = connection;
        }

        @Override
        public void run() {
            new Http11Processor(connection).run();
        }

        private void close() {
            Connector.this.close(connection);
        }
    }
}
