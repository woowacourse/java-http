package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.core.RequestHandlerAdaptor;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_CORE_POOL_SIZE = 0;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 200;
    private static final long DEFAULT_KEEP_ALIVE_TIME_MILLISECONDS = 60000L;

    private final RequestHandlerAdaptor requestHandlerAdaptor;
    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector(final RequestHandlerAdaptor requestHandlerAdaptor) {
        this(requestHandlerAdaptor, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    // TODO Container를 통해 jwp 패키지의 빈 전달하기
    public Connector(
            final RequestHandlerAdaptor requestHandlerAdaptor,
            final int port,
            final int acceptCount,
            final int maxThreads
    ) {
        this.requestHandlerAdaptor = requestHandlerAdaptor;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executorService = createExecutorService(acceptCount, maxThreads);
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ExecutorService createExecutorService(final int acceptCount, final int maxThreads) {
        final var checkedMaxThreads = checkMaxThreads(maxThreads);
        final var checkedAcceptCount = checkAcceptCount(acceptCount);

        return new ThreadPoolExecutor(
                DEFAULT_CORE_POOL_SIZE,
                checkedMaxThreads,
                DEFAULT_KEEP_ALIVE_TIME_MILLISECONDS, TimeUnit.MILLISECONDS,
                new LinkedBlockingQueue<>(checkedAcceptCount)
        );
    }

    public void start() {
        final var thread = new Thread(this);
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            log.info("wait for connect client");
            connect();
        }
    }

    private void connect() {
        try {
            process(serverSocket.accept());
            log.info("socket accepted");
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final var processor = new Http11Processor(connection, requestHandlerAdaptor);
        executorService.submit(processor);
        log.info("thread start");
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (final IOException e) {
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

    private int checkMaxThreads(final int maxThreads) {
        return Math.max(maxThreads, DEFAULT_MAX_THREADS);
    }
}
