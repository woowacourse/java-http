package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import nextstep.servlet.DispatcherServletContainer;
import nextstep.servlet.ServletContainer;
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
    private final ServletContainer container;
    private final Semaphore maxThreadSemaphore;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT);
    }

    public Connector(final ServletContainer container, final int port, final int acceptCount, final int maxThreads) {
        this.container = container;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.maxThreadSemaphore = new Semaphore(maxThreads);
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    public Connector(final int port, final int acceptCount) {
        this.container = new DispatcherServletContainer();
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.maxThreadSemaphore = new Semaphore(DEFAULT_MAX_THREADS);
        this.executorService = Executors.newFixedThreadPool(DEFAULT_MAX_THREADS);
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
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) throws InterruptedException {
        if (connection == null) {
            return;
        }
        maxThreadSemaphore.acquire();
        var processor = new Http11Processor(connection, container.createServlet());
        CompletableFuture.runAsync(processor, executorService)
            .whenCompleteAsync((result, throwable) -> maxThreadSemaphore.release());
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
