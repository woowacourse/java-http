package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MIN_SPARE_THREADS = 10;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 10L;

    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPool;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MIN_SPARE_THREADS, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port,
                     final int acceptCount,
                     final int minSpareThreads,
                     final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.threadPool = new ThreadPoolExecutor(minSpareThreads, maxThreads,
                DEFAULT_KEEP_ALIVE_TIME, TimeUnit.SECONDS, new LinkedBlockingQueue<>(acceptCount));
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

    public Socket connect() throws IOException {
        return serverSocket.accept();
    }

    public void execute(Runnable runnable) {
        threadPool.execute(runnable);
        log.info("pool size: {}", threadPool.getPoolSize());
    }

    public void stop() {
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
