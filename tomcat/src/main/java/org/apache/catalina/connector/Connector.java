package org.apache.catalina.connector;

import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREAD_COUNT = 10;

    private final ServerSocket serverSocket;
    private final ExecutorService pool;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD_COUNT);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        pool = Executors.newFixedThreadPool(maxThreads);
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
        thread.start();
        stopped = false;
        log.info("=================================> [Connector] Daemon Thread Start = {}", thread);
        log.info("=================================> [Connector] Web Application Server started {} port.", serverSocket.getLocalPort());
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
        Http11Processor processor = new Http11Processor(connection);
        pool.execute(processor);
        logThreadPool();
    }

    private void logThreadPool() {
        final ThreadPoolExecutor executor = (ThreadPoolExecutor) pool;
        log.info("=============> Http11Processor START");
        log.info("ThreadPoolExecutor.getClass()             [ ThreadPoolExecutor 클래스 정보      ] = {}", executor.getClass());
        log.info("ThreadPoolExecutor.getThreadFactory()     [ ThreadFactory 클래스 정보           ] = {}", executor.getThreadFactory().getClass());
        log.info("ThreadPoolExecutor.getQueue()             [ 현재 Queue 정보                    ] = {}", executor.getQueue());
        log.info("ThreadPoolExecutor.getPoolSize()          [ 현재 스레드 풀의 스레드 수 정보         ] = {}", executor.getPoolSize());
        log.info("ThreadPoolExecutor.getCorePoolSize()      [ 현재 스레드 풀의 코어 스레드 수 정보     ] = {}", executor.getCorePoolSize());
        log.info("ThreadPoolExecutor.getLargestPoolSize()   [ 현재 스레드 풀의 가장 컸던 스레드 수 정보 ] = {}", executor.getLargestPoolSize());
        log.info("ThreadPoolExecutor.getMaximumPoolSize()   [ 현재 스레드 풀의 최대 스레드 수         ] = {}", executor.getMaximumPoolSize());
        log.info("ThreadPoolExecutor.getActiveCount()       [ 현재 활성 상태인 스레드의 수            ] = {}", executor.getActiveCount());
        log.info("ThreadPoolExecutor.getTaskCount()         [ 현재 스레드 풀이 처리한 총 작업 수       ] = {}", executor.getTaskCount());
        log.info("=============> Http11Processor END");
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
