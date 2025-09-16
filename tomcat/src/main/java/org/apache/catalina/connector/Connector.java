package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.time.Duration;
import java.util.concurrent.ExecutorService;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.http.Http11Processor;

@Slf4j
public class Connector implements Runnable, AutoCloseable {

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 100;
    private static final int TASK_QUEUE_CAPACITY = 100;

    private final ServerSocket serverSocket;
    private final ExecutorService threadPool;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, TASK_QUEUE_CAPACITY);
    }

    public Connector(
            final int port,
            final int acceptCount,
            final int maxThreads,
            final int threadPoolSize
    ) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.threadPool = ThreadPoolFactories.ioBoundFixed(maxThreads, threadPoolSize);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
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
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept());
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }

        threadPool.execute(() -> {
            final var processor = new Http11Processor(connection);
            processor.run();
        });
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            ThreadPoolFactories.gracefulShutdown(threadPool, Duration.ofSeconds(3));
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

    @Override
    public void close() {
        log.info("web server stop.");
        this.stop();
    }
}
