package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
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
    private static final int DEFAULT_MAX_THREADS = 200;

    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor executorService;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executorService = createThreadPool(maxThreads, acceptCount);
    }

    private ThreadPoolExecutor createThreadPool(final int maxThreads, final int acceptCount) {

        /** corePoolSize 설정 기준
         * CPU 바운드 작업: corePoolSize = 코어 수
         * IO 바운드 작업: 크게 설정. corePoolSize = 코어 수 * 2
         * Tomcat 기본 값: corePoolSize = 10
         * */
        int corePoolSize = 10;
        return new ThreadPoolExecutor(
                corePoolSize,       // 코어 스레드 수: 풀에서 항상 유지되는 최소 스레드 수
                maxThreads,         // 최대 스레드 수
                60L,                // 유휴 스레드가 유지되는 시간 = 60초
                TimeUnit.SECONDS,   // 시간 단위
                new LinkedBlockingQueue<>(acceptCount) // acceptCount: 스레드가 모두 바쁘면, 새 연결을 대기시킬 수 있는 큐 크기 = 백로그 큐의 크기
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
        executorService.submit(this);   // Connector 자체도 스레드풀에서 관리.
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될 때까지 대기한다.
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
        var processor = new Http11Processor(connection);
        // new Thread(proccessor).start(); -> 접속자가 많아지면 스레드가 무한정 늘어나 OOM 위험.
        // ThreadPoolExecutor에 작업을 제출
        // 최대 스레드 수(maxThreads)로 동시 실행 제한
        executorService.submit(processor);
    }

    public void stop() {
        stopped = true;
        // 새로운 작업 수락을 중단
        executorService.shutdown();

        try {
            // 기존 작업이 종료될 때까지 최대 60초 대기
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // 기다렸는데도 안 끝나면 즉시 강제 종료
                executorService.shutdownNow();
            }
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
            log.error(e.getMessage(), e);
            Thread.currentThread().interrupt();
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
