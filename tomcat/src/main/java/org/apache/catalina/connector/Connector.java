package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int IDLE_THREAD_COUNT = 150;
    private static final int MAX_THREAD_COUNT = 250;
    private static final int THREAD_KEEP_ALIVE_TIME = 60;
    private static final int TCP_BACKLOG = 50;  // OS에 따라 지켜지지 않을 수 있으며, FIFO가 보장되지 않음

    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPoolExecutor;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, MAX_THREAD_COUNT);
    }

    public Connector(int port, int acceptCount, int maxThreads) {
        int checkedAcceptCount = checkAcceptCount(acceptCount);
        this.threadPoolExecutor = new ThreadPoolExecutor(
                IDLE_THREAD_COUNT, maxThreads,
                THREAD_KEEP_ALIVE_TIME, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(checkedAcceptCount)
        );
        this.serverSocket = createServerSocket(port);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(int port) {
        try {
            final int checkedPort = checkPort(port);
            return new ServerSocket(checkedPort, TCP_BACKLOG);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start(Dispatcher dispatcher) {
        Thread thread = new Thread(() -> run(dispatcher));
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    public void run(Dispatcher dispatcher) {
        // 클라이언트가 연결될 때까지 대기한다.
        while (!stopped) {
            connect(dispatcher);
        }
    }

    private void connect(Dispatcher dispatcher) {
        try {
            process(serverSocket.accept(), dispatcher);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(Socket connection, Dispatcher dispatcher) throws IOException {
        if (connection == null) {
            return;
        }
        Http11Processor processor = new Http11Processor(connection, dispatcher);
        try {
            threadPoolExecutor.execute(processor);
        } catch (RejectedExecutionException e) {
            log.error(e.getMessage(), e);
            connection.close();
            log.warn("Server is busy. Rejected task. {} closed.", connection);
        }
        log.info(
                "Active Thread Count: {} | Queued Task Count : {}",
                threadPoolExecutor.getActiveCount(), threadPoolExecutor.getQueue().size()
        );
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int checkPort(int port) {
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
