package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.container.Container;
import org.apache.coyote.RequestHandlerMapper;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREAD_COUNT = 250;
    private static final int DEFAULT_THREAD_MIN_SPARE = 100;
    private static final int DEFAULT_KEEP_ALIVE_TIME = 1;

    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private final RequestHandlerMapper requestHandlerMapper;
    private boolean stopped;

    public Connector(Container container) {
        this(container, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD_COUNT);
    }

    public Connector(
            final Container container,
            final int port,
            final int acceptCount,
            final int maxThreadCount
    ) {
        this.requestHandlerMapper = container.getRequestHandlerMapper();
        this.pool = createThreadPool(maxThreadCount);
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
    }

    private ExecutorService createThreadPool(int maxThreadCount) {
        //return Executors.newFixedThreadPool(maxThreadCount);
        return new ThreadPoolExecutor(
                DEFAULT_THREAD_MIN_SPARE,
                maxThreadCount,
                DEFAULT_KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(DEFAULT_ACCEPT_COUNT)
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
        var processor = new Http11Processor(connection, requestHandlerMapper);
        pool.execute(processor);
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
