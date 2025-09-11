package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.stopped = false;
        log.info("Connector configured with port: {}, acceptCount: {}, maxThreads: {}",
                serverSocket.getLocalPort(), acceptCount, maxThreads);
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (final IOException e) {
            log.error("Could not create server socker on port {}", port, e);
            throw new UncheckedIOException(e);
        }
    }

    public void start() {
        final var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Connector started and listening on port {}", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될때까지 대기한다.
        log.info("Ready to accept connections...");
        while (!stopped) {
            connect();
        }
        log.info("Connector run loop finished.");
    }

    private void connect() {
        try {
            final Socket connection = serverSocket.accept();
            log.debug("Accepted connection from: {}", connection.getRemoteSocketAddress());
            process(connection);
        } catch (final IOException e) {
            log.error("Error accepting connection", e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }

        log.debug("Submitting connection to processor worker thread.");
        final var processor = new Http11Processor(connection);
        executorService.execute(processor);
    }
    public void stop() {
        log.info("Stopping connector...");
        stopped = true;
        try {
            serverSocket.close();
            shutdownExecutorService();
        } catch (final IOException e) {
            log.error("Error while closing server socket", e);
        }
        log.info("Connector stopped successfully.");
    }

    private void shutdownExecutorService() {
        log.info("Attempting to shut down thread pool...");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                log.warn("Thread pool did not terminate in 5 seconds. Forcing shutdown...");
                executorService.shutdownNow();
            }
        } catch (final InterruptedException e) {
            log.error("Thread pool shutdown was interrupted. Forcing shutdown.", e);
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Thread pool has been shut down.");
    }

    private int checkPort(final int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            log.warn("Invalid port number: {}. Using default port: {}", port, DEFAULT_PORT);
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(final int acceptCount) {
        if (acceptCount <= 0) {
            log.warn("Invalid acceptCount: {}. Using default value: {}", acceptCount, DEFAULT_ACCEPT_COUNT);
            return DEFAULT_ACCEPT_COUNT;
        }
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
