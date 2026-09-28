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
    // Accept-Count는 server socket에서 대기할 최대 요청 개수를 지정한다.
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_QUEUE_CONNECTIONS = 100;

    private static final int DEFAULT_MAX_THREADS = 250;
    private final ServerSocket serverSocket;
    private final ExecutorService executor;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        // 반환된 serverSocket은 최대 acceptCount개 만큼 연결을 대기할 수 있다.
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        // Executors로 FixedThreadPool을 만든다.
        // ExecutorService는 만들어진 풀을 사용하는 인터페이스
        this.executor = new ThreadPoolExecutor(
                10,
                checkMaxThreads(maxThreads),
                // core thread 개수를 넘어선 스레드는 작업 완료 후 0초 후에 제거된다.
                0L,
                TimeUnit.MILLISECONDS,
                new ArrayBlockingQueue<>(DEFAULT_MAX_QUEUE_CONNECTIONS),
                // 작업 풀이 가득 찼거나 종료된 경우, RejectedExecutionException을 던지는 정책
                new ThreadPoolExecutor.AbortPolicy()
        );
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            // 소켓을 열 때, AcceptCount를 넘겨서 대기할 연결 개수를 설정한다.
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
            // serverSocket.accept()는 ServerSocket(listen backlog)에 대기 중인 요청을 가져온다.
            process(serverSocket.accept());
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        try {
            // 요청이 존재하면, 스레드 풀에서 스레드를 할당한다.
            // 반환 값이 없는 실행이므로, execute를 사용하는 게 적절하다.
            executor.execute(new Http11Processor(connection));
        } catch (RejectedExecutionException e) {
            // 풀 종료 중이거나 작업 큐가 가득 찬 경우
            try {
                connection.close();
            } catch (IOException closeError) {
                log.error("거부된 연결을 닫는 중 오류가 발생했습니다", closeError);
            }
        }
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            executor.close();
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

    private int checkMaxThreads(int maxThreads) {
        final var MIN = 1;

        if (maxThreads < MIN || DEFAULT_MAX_THREADS < maxThreads) {
            return DEFAULT_MAX_THREADS;
        }
        return maxThreads;
    }
}
