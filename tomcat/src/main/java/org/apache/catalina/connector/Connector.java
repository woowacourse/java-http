package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.ControllerResolver;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
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
    private static final int DEFAULT_MAX_THREADS = Math.max(2, Runtime.getRuntime().availableProcessors());
    private static final int DEFAULT_MAX_QUEUED_REQUESTS = 100;

    private final ServerSocket serverSocket;
    private final ControllerResolver mapping;
    private final ExecutorService executorService;
    private volatile boolean stopped;

    public Connector(ControllerResolver mapping) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, mapping);
    }

    public Connector(int port, int acceptCount, ControllerResolver mapping) {
        this(port, acceptCount, DEFAULT_MAX_THREADS, mapping);
    }

    public Connector(int port, int acceptCount, int maxThreads, ControllerResolver mapping) {
        this(port, acceptCount, maxThreads, DEFAULT_MAX_QUEUED_REQUESTS, mapping);
    }

    public Connector(int port, int acceptCount, int maxThreads, int maxQueuedRequests, ControllerResolver mapping) {
        this(checkedMaxThreads(maxThreads), checkedQueueSize(maxQueuedRequests),
                createServerSocket(port, acceptCount), mapping);
    }

    Connector(int maxThreads, ServerSocket serverSocket, ControllerResolver mapping) {
        this(maxThreads, DEFAULT_MAX_QUEUED_REQUESTS, serverSocket, mapping);
    }

    Connector(int maxThreads, int maxQueuedRequests, ServerSocket serverSocket, ControllerResolver mapping) {
        this.serverSocket = serverSocket;
        this.mapping = mapping;
        this.executorService = new ThreadPoolExecutor(maxThreads, maxThreads, 0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(maxQueuedRequests));
        this.stopped = false;
    }

    private static int checkedMaxThreads(int maxThreads) {
        if (maxThreads <= 0) {
            throw new IllegalArgumentException("maxThreads는 1 이상이어야 합니다.");
        }
        return maxThreads;
    }

    private static int checkedQueueSize(int maxQueuedRequests) {
        if (maxQueuedRequests <= 0) {
            throw new IllegalArgumentException("maxQueuedRequests는 1 이상이어야 합니다.");
        }
        return maxQueuedRequests;
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
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
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

    private void process(Socket connection) {
        if (connection == null) {
            return;
        }
        Http11Processor processor = new Http11Processor(connection, mapping);
        try {
            executorService.execute(processor);
        } catch (RejectedExecutionException e) {
            respondUnavailable(connection);
        }
    }

    private void respondUnavailable(Socket connection) {
        try (Socket rejectedConnection = connection) {
            HttpResponse response = new HttpResponse();
            response.sendError(HttpStatus.SERVICE_UNAVAILABLE);
            response.addHeader("Connection", "close");
            response.writeTo(rejectedConnection.getOutputStream());
        } catch (IOException e) {
            log.warn("과부하 응답을 전송하지 못했습니다.", e);
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
}
