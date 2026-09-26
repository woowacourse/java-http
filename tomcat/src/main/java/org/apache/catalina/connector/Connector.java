package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int SOCKET_READ_TIMEOUT_MILLIS = 10_000;

    private final ServerSocket serverSocket;
    private final RequestMapping requestMapping;
    private final ExecutorService executorService;
    private final Set<Socket> connections = new HashSet<>();
    private final Object connectionsLock = new Object();
    private volatile boolean stopped;

    public Connector(RequestMapping requestMapping) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, requestMapping);
    }

    public Connector(final int port, final int acceptCount,
                     final int maxThreads, RequestMapping requestMapping) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads는 1 이상이어야 합니다.");
        }

        this.serverSocket = createServerSocket(port, acceptCount);
        this.requestMapping = requestMapping;
        this.executorService = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100)
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
            connection.setSoTimeout(SOCKET_READ_TIMEOUT_MILLIS);
        } catch (SocketException e) {
            log.error("소켓 읽기 제한 시간을 설정하지 못했습니다.", e);
            closeConnection(connection);
            return;
        }

        synchronized (connectionsLock) {
            if (stopped) {
                closeConnection(connection);
                return;
            }
            connections.add(connection);
        }

        try {
            executorService.execute(() -> {
                try {
                    new Http11Processor(connection, requestMapping).run();
                } finally {
                    closeConnection(connection);
                    synchronized (connectionsLock) {
                        connections.remove(connection);
                    }
                }
            });
        } catch (RejectedExecutionException e) {
            log.warn("요청 처리 스레드와 대기열이 모두 찼습니다.", e);
            closeConnection(connection);
            synchronized (connectionsLock) {
                connections.remove(connection);
            }
        }
    }

    public void stop() {
        synchronized (connectionsLock) {
            stopped = true;
            try {
                serverSocket.close();
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            }
            for (Socket connection : connections) {
                closeConnection(connection);
            }
        }
        executorService.shutdown();
    }

    private void closeConnection(final Socket connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.error("연결을 닫지 못했습니다.", e);
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
