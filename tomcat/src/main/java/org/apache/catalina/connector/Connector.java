package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100; // 모든 쓰레드가 사용중일 때 들어오는 연결에 대한 대기열의 길이
    private static final int MAX_THREADS = 250; // 동시에 연결 후 처리 가능한 요청의 최대 개수
    // 최대 ThradPool의 크기는 250, 모든 Thread가 사용 중인(Busy) 상태이면 100명까지 대기 상태로 만들려면 어떻게 할까?
    // acceptCount를 100으로 한다
    // 처리중인 연결이 250개 이므로 나머지는 OS 큐에서 대기하게 된다.
    private static final int SOCKET_TIMEOUT_SECONDS = 10;

    private final ServerSocket serverSocket;
    private boolean stopped;
    private final ExecutorService pool;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.pool = Executors.newFixedThreadPool(maxThreads);
        this.serverSocket = createServerSocket(port, acceptCount);
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
            pool.shutdown();
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) throws SocketException {
        if (connection == null) {
            return;
        }
        connection.setSoTimeout(SOCKET_TIMEOUT_SECONDS * 1000);
        var processor = new Http11Processor(connection);
        pool.execute(processor);
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
