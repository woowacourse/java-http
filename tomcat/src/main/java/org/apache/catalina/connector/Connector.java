package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;
    private static final int MIN_QUEUE_CAPACITY = 1;
    private static final int SOCKET_TIMEOUT_MILLIS = 30_000;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, checkAcceptCount(acceptCount));
        this.executorService = createExecutorService(maxThreads, checkQueueCapacity(acceptCount));
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            return new ServerSocket(checkedPort, acceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ExecutorService createExecutorService(final int maxThreads, final int queueCapacity) {
        // maxThreads(코어=최대 풀 크기)가 모두 사용 중이면 queueCapacity만큼 요청을 대기시킨다.
        return new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                0L,
                TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(queueCapacity)
        );
    }

    private int checkQueueCapacity(final int acceptCount) {
        return Math.max(acceptCount, MIN_QUEUE_CAPACITY);
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
            final Socket connection = serverSocket.accept();
            if (connection != null) {
                // 요청을 보내지 않고 연결만 유지하는 클라이언트가 워커를 계속 점유하지 않도록 읽기 타임아웃을 둔다.
                connection.setSoTimeout(SOCKET_TIMEOUT_MILLIS);
            }
            process(connection);
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
            log.error("스레드 풀과 대기열이 가득 차 요청을 처리할 수 없습니다.", e);
            respondServiceUnavailable(connection);
        }
    }

    private void respondServiceUnavailable(final Socket connection) {
        try {
            final OutputStream outputStream = connection.getOutputStream();
            final HttpResponse response = new HttpResponse();
            response.setStatus(HttpStatus.SERVICE_UNAVAILABLE);
            response.write(outputStream);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        } finally {
            closeQuietly(connection);
        }
    }

    private void closeQuietly(final Socket connection) {
        try {
            connection.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void stop() {
        stopped = true;
        executorService.shutdown();
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
