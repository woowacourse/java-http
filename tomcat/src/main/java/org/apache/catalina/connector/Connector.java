package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final long DEFAULT_KEEP_ALIVE_ALIVE = 60;
    public static final TimeUnit DEFAULT_TIME_UNIT = TimeUnit.SECONDS;

    private final ServerSocket serverSocket;
    private boolean stopped;
    private final ExecutorService executorService;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT);
    }

    public Connector(final int port, final int acceptCount) {
        this.executorService = Executors.newCachedThreadPool();
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        final int coreThreadCount = Runtime.getRuntime().availableProcessors() * 2;
        final BlockingQueue<Runnable> workQueue = new LinkedBlockingQueue<>(DEFAULT_ACCEPT_COUNT);

        this.executorService = new ThreadPoolExecutor(coreThreadCount, maxThreads, DEFAULT_KEEP_ALIVE_ALIVE, DEFAULT_TIME_UNIT, workQueue);
        this.serverSocket = createServerSocket(port, acceptCount);
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
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final var processor = new Http11Processor(connection);
        executorService.submit(processor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
            executorService.shutdown();
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
