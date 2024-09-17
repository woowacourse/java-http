package org.apache.catalina.connector;

import org.apache.catalina.container.Container;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_THREADS = 200;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final Container container;
    private final int acceptCount;

    private boolean stopped;

    public Connector(Container container) {
        this(container, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_THREADS);
    }

    public Connector(
            final Container container,
            final int port,
            final int acceptCount,
            final int maxThreads
    ) {
        this.container = container;
        this.acceptCount = checkAcceptCount(acceptCount);
        this.serverSocket = createServerSocket(port, this.acceptCount);
        this.threadPoolExecutor = new TomcatThreadPool(checkMaxThreads(maxThreads), this.acceptCount);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int checkedAcceptCount) {
        try {
            return new ServerSocket(checkPort(port), checkedAcceptCount);
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
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect();
        }
    }

    private void connect() {
        try {
            accept();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void accept() throws IOException {
        // acceptCount 미만인 경우 연결을 accept한다.
        final var nowQueueSize = threadPoolExecutor.getQueue().size();
        if (nowQueueSize < acceptCount) {
            process(serverSocket.accept());
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }

        threadPoolExecutor.execute(container.acceptConnection(connection));
    }

    public void stop() {
        stopped = true;
        threadPoolExecutor.shutdown();
        try {
            threadPoolExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
        } finally {
            threadPoolExecutor.shutdownNow();
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
        return Math.min(acceptCount, DEFAULT_ACCEPT_COUNT);
    }

    private int checkMaxThreads(final int maxThreads) {
        return Math.min(maxThreads, DEFAULT_THREADS);
    }

    public int getActiveConnect() {
        return threadPoolExecutor.getActiveCount();
    }

    public int getWaitConnect() {
        return threadPoolExecutor.getQueue().size();
    }
}
