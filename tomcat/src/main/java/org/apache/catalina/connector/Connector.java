package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http11.Http11Processor;
import org.apache.catalina.controller.ControllerMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);
    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int MAX_THREADS_SIZE = 10;
    private static final int MIN_PORT = 1;
    private static final int MAX_PORT = 65535;
    private static final String SERVER_START_MESSAGE = "Web Application Server started {} port.";
    public static final int TASK_AWAIT_TIME = 60;

    private final ServerSocket serverSocket;
    private final ControllerMapper controllerMapper;
    private boolean stopped;
    private final ExecutorService executorService;

    public Connector(final ControllerMapper controllerMapper) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, controllerMapper, MAX_THREADS_SIZE);
    }

    public Connector(final int port, final int acceptCount, final ControllerMapper controllerMapper) {
        this(port, acceptCount, controllerMapper, MAX_THREADS_SIZE);
    }

    public Connector(
            final int port,
            final int acceptCount,
            final ControllerMapper controllerMapper,
            final int maxThreads
    ) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.controllerMapper = controllerMapper;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
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
        final var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info(SERVER_START_MESSAGE, serverSocket.getLocalPort());
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
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final var processor = new Http11Processor(connection, controllerMapper);
        executorService.execute(processor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
            shutdown();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void shutdown () {
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(TASK_AWAIT_TIME, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                if (!executorService.awaitTermination(TASK_AWAIT_TIME, TimeUnit.SECONDS)) {
                    log.error("Pool did not terminate");
                }
            }
        } catch (final InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
    }

    private int checkPort(final int port) {
        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(final int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
