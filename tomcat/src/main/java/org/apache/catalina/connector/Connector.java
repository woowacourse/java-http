package org.apache.catalina.connector;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.Adapter;
import org.apache.catalina.SessionManager;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_THREAD_POOL_SIZE = 250;
    private static final int DEFAULT_WORK_QUEUE_CAPACITY = 100;

    private final ServerSocket serverSocket;
    private final Adapter adapter;
    private final SessionManager sessionManager = new SessionManager();
    private final ExecutorService executorService;
    private volatile boolean stopped;

    public Connector(Adapter adapter) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, adapter, DEFAULT_THREAD_POOL_SIZE);
    }

    public Connector(final int port, final int acceptCount, final Adapter adapter, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.adapter = adapter;
        this.executorService = new ThreadPoolExecutor(
                maxThreads, maxThreads,
                0L, TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(DEFAULT_WORK_QUEUE_CAPACITY),
                new ThreadPoolExecutor.AbortPolicy()
        );
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
        var processor = new Http11Processor(connection, adapter, sessionManager);
        try {
            executorService.execute(processor);
        } catch (RejectedExecutionException e) {
            log.warn("Request processing rejected: worker queue is full or executor is shut down");
            sendServiceUnavailableAndClose(connection);
        }
    }

    private void sendServiceUnavailableAndClose(Socket connection) {
        try (connection) {
            var outputStream = connection.getOutputStream();
            outputStream.write(HttpResponse.serviceUnavailable().toBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error("Failed to send 503 response or close rejected connection", e);
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
