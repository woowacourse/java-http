package org.apache.catalina.connector;

import org.apache.catalina.routing.RequestMapping;
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
    private static final int SOCKET_READ_TIMEOUT_MILLIS = 5_000;
    private static final long SHUTDOWN_TIMEOUT_SECONDS = 10;

    private final ServerSocket serverSocket;
    private final RequestMapping requestMapping;
    private final ExecutorService executorService;

    private volatile boolean stopped;

    public Connector(RequestMapping requestMapping, int maxThreads) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, requestMapping, maxThreads);
    }

    public Connector(final int port, final int acceptCount, final RequestMapping requestMapping, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.requestMapping = requestMapping;
        this.executorService = new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(100)
        );
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
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }

        executorService.shutdown();
        try {
            if (executorService.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.info("모든 요청 처리가 완료되었습니다.");
            } else {
                log.warn("제한 시간 안에 모든 요청 처리가 끝나지 않았습니다.");
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.warn("요청 처리 종료 대기가 중단되었습니다.");
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

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (IOException e) {
            if (!stopped) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private void process(final Socket connection) throws IOException {
        if (connection == null) {
            return;
        }
        connection.setSoTimeout(SOCKET_READ_TIMEOUT_MILLIS);
        var processor = new Http11Processor(requestMapping, connection);
        try {
            executorService.submit(processor);
        } catch (RejectedExecutionException re) {
            connection.close();
            log.warn("요청 처리 작업이 거부되었습니다.");
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
