package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.apache.util.CustomTaskQueue;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_CORE_THREADS = 10;
    private static final int DEFAULT_MAX_THREADS = 200;
    private static final int DEFAULT_QUEUE_SIZE = 100;
    private static final int SHUTDOWN_TIMEOUT = 60;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private volatile boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_CORE_THREADS, DEFAULT_MAX_THREADS, DEFAULT_QUEUE_SIZE);
    }

    public Connector(
            final int port,
            final int acceptCount,
            final int coreThreads,
            final int maxThreads,
            final int queueSize
    ) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executorService = createThreadPool(coreThreads, maxThreads, queueSize);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final var checkedPort = checkPort(port);
            final var checkedAcceptCount = checkAcceptCount(acceptCount);

            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ExecutorService createThreadPool(final int coreThreads, final int maxThreads, final int queueSize) {
        final var queue = new CustomTaskQueue(queueSize);
        final var executor = new ThreadPoolExecutor(
                coreThreads,
                maxThreads,
                60L, TimeUnit.SECONDS,
                queue,
                new ThreadPoolExecutor.AbortPolicy()
        );
        queue.setParent(executor);

        return executor;
    }

    public void start() {
        final var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
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
            if (!stopped) {
                log.error("Error accepting connection", e);
            }
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final var processor = new Http11Processor(connection);
        executorService.execute(processor);
    }

    public void stop() {
        stopped = true;
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
