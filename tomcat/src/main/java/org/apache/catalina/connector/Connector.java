package org.apache.catalina.connector;

import org.apache.catalina.controller.RequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.response.Http11ErrorResponder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int DEFAULT_MAX_QUEUE_SIZE = 100;

    private final ServerSocket serverSocket;
    private volatile boolean stopped;
    private final RequestMapping requestMapping;
    private final ExecutorService executorService;

    public Connector(final RequestMapping requestMapping) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, DEFAULT_MAX_QUEUE_SIZE,requestMapping);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads, final int maxQueueSize, final RequestMapping requestMapping) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.requestMapping = requestMapping;
        this.executorService = new ThreadPoolExecutor(
                maxThreads, maxThreads,
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(maxQueueSize),
                new ThreadPoolExecutor.AbortPolicy());
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
        var processor = new Http11Processor(connection, requestMapping);
        try {
            executorService.execute(processor);
        } catch (RejectedExecutionException e) {
            log.warn("대기 큐가 가득 차서 연결을 종료합니다.");
            Http11ErrorResponder.sendServiceUnavailable(connection);
            try {
                connection.close();
            } catch (IOException e1) {
                log.error(e.getMessage(), e1);
            }
        }
    }

    public void stop() {
        stopped = true;
        executorService.shutdown();
        try {
            serverSocket.close();
        } catch (IOException e) {
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
