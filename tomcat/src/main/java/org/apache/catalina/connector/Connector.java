package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.ControllerContainer;
import org.apache.catalina.ControllerFactory;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.Container;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREAD_SIZE = 100;
    private static final int AWAIT_TIME = 800;

    private final ServerSocket serverSocket;
    private final Container container;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector() {
        this(new ControllerContainer(new RequestMapping(), ControllerFactory.createExceptionControllers()),
                DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD_SIZE);
    }

    public Connector(final Container container, final int port, final int acceptCount, final int maxThreads) {
        this.container = container;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executorService = Executors.newFixedThreadPool(maxThreads);
        this.stopped = false;
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
        executorService.submit(this);
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
        var processor = new Http11Processor(connection, container);
        new Thread(processor).start();
    }

    public void stop() {
        stopped = true;
        executorService.shutdown();
        try {
            serverSocket.close();
            shutDownExecutorAwait(AWAIT_TIME);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
    }

    private void shutDownExecutorAwait(final int millis) throws InterruptedException {
        if (!executorService.awaitTermination(millis, TimeUnit.MILLISECONDS)) {
            executorService.shutdownNow();
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
