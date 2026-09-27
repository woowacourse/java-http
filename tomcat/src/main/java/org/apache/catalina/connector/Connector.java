package org.apache.catalina.connector;

import org.apache.coyote.Adapter;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int DEFAULT_MAX_QUEUED_REQUESTS = 100;
    private static final int DEFAULT_READ_TIMEOUT_MILLIS = 30_000;
    private static final int DEFAULT_QUEUE_WAIT_TIMEOUT_MILLIS = 30_000;
    private static final int DEFAULT_SHUTDOWN_TIMEOUT_MILLIS = 5_000;

    private final ServerSocket serverSocket;
    private final Adapter adapter;
    private final ThreadPoolExecutor executorService;
    private final ScheduledThreadPoolExecutor timeoutExecutor;
    private final Set<Socket> openConnections = ConcurrentHashMap.newKeySet();
    private final Limits limits;
    private volatile boolean stopped;

    record Limits(int maxThreads, int maxQueuedRequests, int readTimeoutMillis,
                  int queueWaitTimeoutMillis, int shutdownTimeoutMillis) {
        Limits {
            if (maxThreads <= 0 || maxQueuedRequests <= 0 || readTimeoutMillis <= 0
                    || queueWaitTimeoutMillis <= 0 || shutdownTimeoutMillis <= 0) {
                throw new IllegalArgumentException("Connector 제한 값은 0보다 커야 합니다.");
            }
        }
    }

    public Connector(Adapter adapter) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, adapter);
    }

    public Connector(
            final int port,
            final int acceptCount,
            final int maxThreads,
            final Adapter adapter
    ) {
        this(port, acceptCount, adapter, new Limits(maxThreads, DEFAULT_MAX_QUEUED_REQUESTS,
                DEFAULT_READ_TIMEOUT_MILLIS, DEFAULT_QUEUE_WAIT_TIMEOUT_MILLIS,
                DEFAULT_SHUTDOWN_TIMEOUT_MILLIS));
    }

    Connector(final int port, final int acceptCount, final Adapter adapter, final Limits limits) {
        this.adapter = Objects.requireNonNull(adapter);
        this.limits = Objects.requireNonNull(limits);
        // acceptCount limits connections before accept; this queue holds accepted connections.
        this.executorService = new ThreadPoolExecutor(limits.maxThreads(), limits.maxThreads(),
                0L, TimeUnit.MILLISECONDS, new ArrayBlockingQueue<>(limits.maxQueuedRequests()));
        this.timeoutExecutor = new ScheduledThreadPoolExecutor(1, runnable -> {
            Thread thread = new Thread(runnable, "connector-timeout");
            thread.setDaemon(true);
            return thread;
        });
        this.timeoutExecutor.setRemoveOnCancelPolicy(true);
        this.serverSocket = createServerSocket(port, acceptCount);
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
        long checkInterval = Math.min(1_000, Math.max(1, limits.queueWaitTimeoutMillis() / 2));
        timeoutExecutor.scheduleWithFixedDelay(this::expireQueuedRequests,
                checkInterval, checkInterval, TimeUnit.MILLISECONDS);
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
            if (!stopped) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        if (stopped) {
            closeConnection(connection);
            return;
        }
        try {
            connection.setSoTimeout(limits.readTimeoutMillis());
            openConnections.add(connection);
            executorService.execute(new ConnectionTask(connection));
        } catch (RejectedExecutionException e) {
            closeConnection(connection);
            if (!stopped) {
                log.warn("Request rejected because the connector queue is full.");
            }
        } catch (IOException e) {
            closeConnection(connection);
            log.warn("Failed to configure a connection timeout.", e);
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
            if (!executorService.awaitTermination(limits.shutdownTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                forceStop();
                if (!executorService.awaitTermination(limits.shutdownTimeoutMillis(), TimeUnit.MILLISECONDS)) {
                    log.warn("Connector workers did not stop within the shutdown timeout.");
                }
            }
        } catch (InterruptedException e) {
            forceStop();
            Thread.currentThread().interrupt();
        } finally {
            timeoutExecutor.shutdownNow();
        }
    }

    private void expireQueuedRequests() {
        for (Runnable queued : executorService.getQueue()) {
            ConnectionTask task = (ConnectionTask) queued;
            if (task.hasTimedOut() && executorService.remove(task)) {
                closeConnection(task.connection);
            }
        }
    }

    int queuedRequestCount() {
        return executorService.getQueue().size();
    }

    boolean workersTerminated() {
        return executorService.isTerminated();
    }

    private void forceStop() {
        executorService.shutdownNow();
        for (Socket connection : openConnections) {
            closeConnection(connection);
        }
    }

    private void closeConnection(final Socket connection) {
        openConnections.remove(connection);
        try {
            connection.close();
        } catch (IOException e) {
            log.warn("Failed to close a connection.", e);
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

    private final class ConnectionTask implements Runnable {

        private final Socket connection;
        private final long queuedAt = System.nanoTime();

        private ConnectionTask(Socket connection) {
            this.connection = connection;
        }

        private boolean hasTimedOut() {
            return System.nanoTime() - queuedAt >= TimeUnit.MILLISECONDS.toNanos(limits.queueWaitTimeoutMillis());
        }

        @Override
        public void run() {
            try {
                if (hasTimedOut()) {
                    closeConnection(connection);
                    return;
                }
                new Http11Processor(connection, adapter, timeoutExecutor).run();
            } finally {
                closeConnection(connection);
            }
        }
    }
}
