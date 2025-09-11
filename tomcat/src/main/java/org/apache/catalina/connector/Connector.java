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
import org.apache.catalina.servlet.ServletContainer;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 200;
    private static final int DEFAULT_QUEUE_CAPACITY = 100;

    private final ServletContainer container;
    private final ServerSocket serverSocket;

    /*
    기존에는 process()에서 요청마다 new Thread(processor).start()를 했음
    이 경우 동시 요청 폭주시 스레드 무한 생성 -> 서버 다운으로 이어질 수 있음
    따라서 ThreadPoolExecutor를 통해 스레드풀 생성
     */
    private final ExecutorService threadPool;
    private volatile boolean stopped;

    public Connector(ServletContainer container) {
        this(container, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS, DEFAULT_QUEUE_CAPACITY);
    }

    public Connector(final ServletContainer container, final int port, final int acceptCount,
                     final int maxThreads, final int queueCapacity) {
        this.container = container;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        /*
        Executors.newFixedThreadPool()의 경우에도 내부 구현은 new ThreadPoolExecutor()를 사용함
        단 대기 큐 사이즈의 제한이 없고 별도의 스레드 종료가 없음.
        요청 폭주시 큐가 무제한이 되면 메모리가 오버되며 서버가 다운되는 위험성이 여전히 존재함
        따라서 직접 new ThreadPoolExecutor를 통해 생성하여 대기큐 사이즈 제한과 스레드 종료시간을 설정해줌
         */
        this.threadPool = new ThreadPoolExecutor(
                maxThreads,               // corePoolSize
                maxThreads,               // maximumPoolSize
                60L, TimeUnit.SECONDS,    // 스레드 종료 시간
                new ArrayBlockingQueue<>(queueCapacity),  // 큐 크기를 제한하여 요청 큐 생성
                new ThreadPoolExecutor.AbortPolicy()      // 큐가 가득하면 예외를 던지도록 설정
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
        var processor = new Http11Processor(connection, container);
        try {
            threadPool.execute(processor);
        } catch (RejectedExecutionException e) {
            log.warn("Thread pool saturated, rejecting request: {}", e.getMessage());
            try {
                connection.close(); // 소켓 누수 방지
            } catch (IOException ex) {
                log.error("Failed to close rejected socket", ex);
            }
        }
    }


    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
        // 서버 종료 시 실행 중인 모든 스레드를 강제 종료
        threadPool.shutdownNow();
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
