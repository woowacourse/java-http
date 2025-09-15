package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREAD_COUNT = 10;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD_COUNT);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        // 여기서 고정된 크기의 스레드 풀 생성
        this.executorService = Executors.newFixedThreadPool(maxThreads);
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

    // 메서드 요청마다 스레드 새로 생성 중.
    // 요청마다 새 스레드를 생성하지 말고, 미리 스레드를 생성 후 스레드 풀에 넣어둠.
    // 요청이 오면 해당 스레드 풀에서 꺼내 쓴다.
    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection);

        // 스레드를 생성하지 말고, 스레드 풀에 작업을 submit 함.
        executorService.submit(processor);
    }

    // 서버가 멈추면 스레드풀도 없어져야 함.
    public void stop() {
        stopped = true;
        try {
            // 스레드풀 종료
            executorService.shutdown();
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
