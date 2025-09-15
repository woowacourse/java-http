package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    // 목표 CPU 사용률 = 70
    private static final double TARGET_CPU_UTILIZATION = 0.7;
    // 평균 대기 시간 = 984.24 마이크로초
    private static final int AVERAGE_WAITING_TIME = 984;
    // 평균 서비스 시간 = 2,846.79 마이크로초
    private static final int AVERAGE_SERVICE_TIME = 2847;
    // 최대 스레드 수 = CPU 코어 수 * 목표 CPU 사용률 * (1 + 평균 대기 시간 / 평균 서비스 시간)
    private static final int CORE_THREADS_SIZE =
            (int) Math.round(
                    Runtime.getRuntime().availableProcessors() * TARGET_CPU_UTILIZATION *
                            (1 + (double) AVERAGE_WAITING_TIME / AVERAGE_SERVICE_TIME)
            );

    // 임시로 설정 후, 서비스를 운영하면서 조정 필요
    private static final int MAX_THREADS_SIZE = CORE_THREADS_SIZE * 4;

    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPoolExecutor;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT);
    }

    public Connector(final int port, final int acceptCount) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.threadPoolExecutor = new ThreadPoolExecutor(
                CORE_THREADS_SIZE,
                MAX_THREADS_SIZE,
                60L,
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
        this.threadPoolExecutor.execute(new Http11Processor(connection));
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
