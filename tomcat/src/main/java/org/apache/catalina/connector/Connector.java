package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http11.Dispatcher;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int MAX_THREAD_POOL_COUNT = 10;

    private final ServerSocket serverSocket;
    private final Dispatcher dispatcher;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector(Dispatcher dispatcher) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, MAX_THREAD_POOL_COUNT, dispatcher);
    }

    public Connector(final int port, final int acceptCount, final int maxThread, final Dispatcher dispatcher) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executorService = new ThreadPoolExecutor(
                maxThread,
                maxThread,
                60L,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(DEFAULT_ACCEPT_COUNT)
        );
        this.dispatcher = dispatcher;
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
            executorService.execute(this::connect);
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept(), dispatcher);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection, final Dispatcher dispatcher) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, dispatcher);
        processor.process(connection);
        close(connection);
    }

    private void close(final Socket connection) {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("soket을 닫는 과정에서 에러가 발생했습니다. : ", exception);
        }
    }

    public void stop() {
        stopped = true;
        executorService.shutdownNow();
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
