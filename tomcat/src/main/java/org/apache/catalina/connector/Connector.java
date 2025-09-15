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
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MIN_SPARE_THREADS = 10;
    private static final int DEFAULT_MAX_THREADS = 200;
    private static final int DEFAULT_THREADS_MAX_IDLE_TIME = 60;

    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private volatile boolean stopped;

    public Connector() {
        this(
                DEFAULT_PORT,
                DEFAULT_ACCEPT_COUNT,
                DEFAULT_MIN_SPARE_THREADS,
                DEFAULT_MAX_THREADS,
                DEFAULT_THREADS_MAX_IDLE_TIME
        );
    }

    public Connector(
            final int port,
            final int acceptCount,
            final int minSpareThreads,
            final int maxThreads,
            final int threadsMaxIdleTime
    ) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executor = new ThreadPoolExecutor(
                minSpareThreads,
                maxThreads,
                threadsMaxIdleTime,
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
            if (stopped || serverSocket.isClosed()) {
                log.info("서버가 정상적으로 종료되었습니다.");
                return;
            }
            log.error("클라이언트 연결 처리 중 오류: {}", e.getMessage());
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection);
        executor.execute(processor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("서버 소켓 종료 중 오류: {}", e.getMessage());
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
