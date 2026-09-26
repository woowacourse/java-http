package org.apache.catalina.connector;

import org.apache.catalina.Manager;
import org.apache.catalina.controller.RequestMapping;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int DEFAULT_QUEUED_REQUESTS = 100;
    private static final long SHUTDOWN_WAIT_SECONDS = 1;

    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private final Set<Socket> connections = ConcurrentHashMap.newKeySet();
    private final Manager sessionManager = new SessionManager();
    private final RequestMapping requestMapping;
    private volatile boolean stopped;

    public Connector(RequestMapping requestMapping) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, requestMapping);
    }

    public Connector(final int port, final int acceptCount, final RequestMapping requestMapping) {
        this(port, acceptCount, DEFAULT_MAX_THREADS, requestMapping);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads,
                     final RequestMapping requestMapping) {
        this(port, acceptCount, maxThreads, DEFAULT_QUEUED_REQUESTS, requestMapping);
    }

    Connector(final int port, final int acceptCount, final int maxThreads,
              final int queuedRequests, final RequestMapping requestMapping) {
        if (maxThreads <= 0 || queuedRequests <= 0) {
            throw new IllegalArgumentException("스레드 수와 대기 작업 수는 양수여야 합니다.");
        }
        this.requestMapping = requestMapping;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executor = new ThreadPoolExecutor(maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(queuedRequests), Executors.defaultThreadFactory());
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
        if (stopped) {
            throw new IllegalStateException("종료된 Connector는 다시 시작할 수 없습니다.");
        }
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

    void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, sessionManager, requestMapping);
        connections.add(connection);
        try {
            executor.execute(() -> {
                try {
                    processor.run();
                } finally {
                    closeConnection(connection);
                }
            });
        } catch (RejectedExecutionException e) {
            closeConnection(connection);
            log.warn("요청 처리 풀이 가득 차거나 종료되어 연결을 닫았습니다.");
        }
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        executor.shutdown();
        try {
            if (!executor.awaitTermination(SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS)) {
                forceStop();
                if (!executor.awaitTermination(SHUTDOWN_WAIT_SECONDS, TimeUnit.SECONDS)) {
                    log.warn("요청 처리 스레드가 종료 제한 시간 내에 끝나지 않았습니다.");
                }
            }
        } catch (InterruptedException e) {
            forceStop();
            Thread.currentThread().interrupt();
        }
    }

    private void forceStop() {
        executor.shutdownNow();
        for (Socket connection : connections) {
            closeConnection(connection);
        }
    }

    private void closeConnection(Socket connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            connections.remove(connection);
        }
    }

    private int checkPort(final int port) {
        final var MIN_PORT = 0;
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
