package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_CORE_POLL_SIZE = 2;
    private static final int DEFAULT_MAX_THREADS = 100; // 기본은 200, t4g.small 기준 설정

    private final ServerSocket serverSocket;
    private boolean stopped;
    private final ExecutorService executorService;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executorService = new ThreadPoolExecutor(
                DEFAULT_CORE_POLL_SIZE, // corePoolSize(항상 유지할 스레드 수)
                maxThreads, // maximumPoolSize(최대 스레드 수)
                0L, TimeUnit.MILLISECONDS, // keepAliveTime(core 초과 스레드가 대기할 시간)
                new ArrayBlockingQueue<>(acceptCount), // 대기 큐 크기 제한
                new ThreadPoolExecutor.AbortPolicy() // 거부 정책(풀과 큐가 모두 찼을 때 새 작업 처리 정책)
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

        executorService.execute(() -> {
            try {
                var processor = new Http11Processor(connection);
                processor.run();
            } catch (Exception e) {
                log.error("HTTP 요청 중 예외 발생, client={}, port={}",
                        connection.getInetAddress(), connection.getPort(), e);
            }
        });
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            executorService.shutdown(); // 서버 종료 시 스레드풀 정리 (실행 중인 작업은 계속 실행)
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow(); // 5초 안에 종료되지 않으면 강제 종료
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
                Thread.currentThread().interrupt();
            }
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
