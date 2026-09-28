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
    private static final int EXECUTOR_TERMINATION_TIMEOUT_SECONDS = 5;

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

    public Connector(final int port, final int acceptCount, final int maxThreads, final int maxQueueSize) {
        validatePort(port);
        validateAcceptCount(acceptCount);
        validateMaxThreads(maxThreads);
        validateMaxQueueSize(maxQueueSize);
        this.executorService = createExecutorService(maxThreads, maxQueueSize);
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
    }

    private ExecutorService createExecutorService(final int maxThreads, final int maxQueueSize) {
        return new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(maxQueueSize),
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            return new ServerSocket(port, acceptCount);
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
        var processor = new Http11Processor(connection);
        try {
            executorService.execute(processor);
        } catch (RejectedExecutionException e) {
            log.warn("Request rejected because the thread pool and work queue are full.");
            close(connection);
        }
    }

    private void close(final Socket connection) {
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
        shutdownExecutorService();
    }

    private void shutdownExecutorService() {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(EXECUTOR_TERMINATION_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                    log.warn("ExecutorService did not terminate.");
                }
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private void validatePort(final int port) {
        if (port < 0 || 65535 < port) {
            throw new IllegalArgumentException("port는 0 이상 65535 이하여야 합니다.");
        }
    }

    private void validateAcceptCount(final int acceptCount) {
        if (acceptCount <= 0) {
            throw new IllegalArgumentException("acceptCount는 0보다 커야 합니다.");
        }
    }

    private void validateMaxThreads(final int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads는 0보다 커야 합니다.");
        }
    }

    private void validateMaxQueueSize(final int maxQueueSize) {
        if (maxQueueSize <= 0) {
            throw new IllegalArgumentException("maxQueueSize는 0보다 커야 합니다.");
        }
    }
}
