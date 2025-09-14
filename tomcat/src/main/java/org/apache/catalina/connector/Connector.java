package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.ContextConfig;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int CORE_POOL_SIZE = 10;
    private static final long KEEP_ALIVE_TIME = 60L;

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_THREAD_FULL = 250;
    private static final int DEFAULT_QUEUE_SIZE = 100;

    private final ExecutorService executorService;
    private final ServerSocket serverSocket;
    private final CoyoteAdapter adapter;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_THREAD_FULL, DEFAULT_QUEUE_SIZE);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads, final int queueSize) {
        this.executorService = createThreadPool(maxThreads, queueSize);
        this.serverSocket = createServerSocket(port, acceptCount, maxThreads);
        this.adapter = new CoyoteAdapter(ContextConfig.CATALINA_CONTAINER);
        this.stopped = false;
    }

    private static ExecutorService createThreadPool(final int maxThreads, final int queueSize) {
        return new ThreadPoolExecutor(
                CORE_POOL_SIZE,
                maxThreads,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(queueSize)
        );
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount, final int maxThreads) {
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
        log.info("WAS started PORT: {}, MAX_THREAD: {}",
                serverSocket.getLocalPort(),
                ((ThreadPoolExecutor) executorService).getMaximumPoolSize());
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

        executorService.submit(() -> {
            var processor = new Http11Processor(connection, adapter);
            processor.run();
        });
    }

    public void stop() {
        stopped = true;

        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(30, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }

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
