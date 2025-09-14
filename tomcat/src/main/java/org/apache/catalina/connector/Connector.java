package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int SYSTEM_PROCESSOR_COUNT = Runtime.getRuntime().availableProcessors();

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT);
    }

    public Connector(final int port, final int acceptCount) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.executorService = new ThreadPoolExecutor(
            SYSTEM_PROCESSOR_COUNT * 3, // 현재 복잡한 요청 처리가 없어 1초 동안 1개의 코어로 수많은 작업 처리가 가능
            SYSTEM_PROCESSOR_COUNT * 10,
            60L, TimeUnit.SECONDS,
            new ArrayBlockingQueue<>(acceptCount),
            new ThreadPoolExecutor.AbortPolicy()
        );
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
        var processor = new Http11Processor(connection);
        try {
            executorService.execute(processor);
        } catch (RejectedExecutionException e) {
            log.error(e.getMessage(), e);
            try {
                connection.close();
            } catch (IOException ioe) {
                log.error(ioe.getMessage(), ioe);
            }
        }
    }

    public void stop() {
        stopped = true;
        try {
            // 스레드 풀에서 작업을 거부해도 Socket은 열려 있어 리소스 누수 방지를 위해 호출 필요
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        // graceful shutdown
        shutdownAndAwaitTermination(executorService);
    }

    /**
     * graceful shutdown
     * - 문제 없이 서비스를 안정적으로 종료하는 방식
     */
    private static void shutdownAndAwaitTermination(final ExecutorService service) {
        // non-blocking, 새로운 작업 받지 않음
        // 처리 중이거나 대기중인 작업을 처리하고 스레드 풀의 자원을 정리
        service.shutdown();

        try {
            // 대기중인 작업들을 모두 완료할 때 까지 10초 대기
            if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                service.shutdownNow(); // 강제 종료 시도
                // 작업이 취소될 때 까지 대기
                if (!service.awaitTermination(10, TimeUnit.SECONDS)) {
                    log.error("스레드 풀이 종료되지 않았습니다.");
                }
            }
        } catch (InterruptedException e) {
            log.error("인터럽트 발생: {}", e.getMessage());
            throw new RuntimeException(e);
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
