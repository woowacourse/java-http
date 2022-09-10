package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_BACKLOG_COUNT = 100;
    private static final int DEFAULT_POOL_SIZE = 250;
    private static final int DEFAULT_CORE_SIZE = 50;
    private static final long DEFAULT_KEEP_ALIVE_TIME = 120L;

    private final ServerSocket serverSocket;
    private boolean stopped;
    private final ExecutorService executorService;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_BACKLOG_COUNT, DEFAULT_POOL_SIZE, DEFAULT_CORE_SIZE, DEFAULT_KEEP_ALIVE_TIME);
    }

    public Connector(final int port,
                     final int backlogCount,
                     final int maxThreadSize,
                     final int coreThreadSize,
                     final long keepAliveTimeSecond) {
        this.serverSocket = createServerSocket(port, backlogCount);
        this.stopped = false;
        this.executorService = new ThreadPoolExecutor(
                maxThreadSize,
                coreThreadSize,
                keepAliveTimeSecond,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>()
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
        var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
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

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        var processor = new Http11Processor(connection);
        executorService.submit(processor);
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
        return Math.max(acceptCount, DEFAULT_BACKLOG_COUNT);
    }
}
