package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.FrontController;
import org.apache.coyote.http11.processor.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {
    private static final FrontController frontController = new FrontController();
    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final long KEEP_ALIVE = 30L;
    private static final int DEFAULT_CORE_POOL_SIZE = 8;
    private static final int DEFAULT_MAX_THREADS = 16;

    private final ThreadPoolExecutor threadPool;
    private final ServerSocket serverSocket;
    private volatile boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_CORE_POOL_SIZE, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int corePoolSize, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.threadPool = new ThreadPoolExecutor(
                corePoolSize,
                maxThreads,
                KEEP_ALIVE,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(acceptCount)
        );

        this.threadPool.setRejectedExecutionHandler((r, executor) -> {
            log.error("큐가 곽차서 더이상 작업을 받을 수 없습니다. 작업 클래스={}", r.getClass().getName());
        });
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
        Thread thread = new Thread(this);
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
        threadPool.submit(new Http11Processor(connection, frontController));
    }

    public void stop() {
        stopped = true;
        threadPool.shutdown(); //그러면, 새로운 작업은 막고, 이미 진행중인 작업은 실행
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
