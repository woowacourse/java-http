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
import org.apache.coyote.http11.RequestMappings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    public static final int DEFAULT_MAX_THREADS = 250;

    private final ServerSocket serverSocket;
    private boolean stopped;

    private final RequestMappings requestMappings;

    private final ExecutorService threadPool;

    public Connector(RequestMappings requestMappings) {
        this(requestMappings, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    private Connector(RequestMappings requestMappings, int maxThreads) {
        this(requestMappings, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, maxThreads);
    }

    private Connector(RequestMappings requestMappings, final int port, final int acceptCount, final int maxThreads) {
        this.requestMappings = requestMappings;

        threadPool = new ThreadPoolExecutor(
                maxThreads, // maxThreads 의 고정된 스레드 개수를 가진 풀 생성
                maxThreads, // maxThreads 의 고정된 스레드 개수를 가진 풀 생성
                0L, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<Runnable>(acceptCount)
        );
        // 단, 처음 maxThreads개의 스레드가 생기기 전에는 새로 생성한다.
        //acceptCount는 최대 연결 대기 상태의 수다.
        serverSocket = createServerSocket(port, acceptCount);
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
        Thread thread = new Thread(this);
        thread.setDaemon(true);
        /*
         * 데몬 스레드는 주 스레드의 어떤 기능을 보조하는 목적으로 사용하는 스레드로 주 스레드가 종료되면 같이 종료된다.
         * 여기서 주 스레드가 종료되면 당연히 더이상 서버와의 연결을 수락해선 안되므로 데몬 스레드로 설정한다.
         * */
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
        var processor = new Http11Processor(connection, requestMappings);
        threadPool.execute(processor);
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
