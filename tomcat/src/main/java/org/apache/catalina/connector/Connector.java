package org.apache.catalina.connector;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.ThreadPoolExecutor.AbortPolicy;
import java.util.concurrent.TimeUnit;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.message.HttpResponse;
import org.apache.coyote.message.HttpStatusCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_CORE_THREAD_COUNT = 2;
    private static final int DEFAULT_MAX_THREAD_COUNT = 10;

    private final ServerSocket serverSocket;
    private final ExecutorService threadPoolExecutor;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD_COUNT);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        final int checkedPort = checkPort(port);
        final int checkedAcceptCount = checkAcceptCount(acceptCount);
        final int checkedMaxThreads = checkMaxThreads(maxThreads);

        this.serverSocket = createServerSocket(checkedPort, checkedAcceptCount);
        this.stopped = false;
        this.threadPoolExecutor = createThreadPool(checkedAcceptCount, checkedMaxThreads);
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            return new ServerSocket(port, acceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    private ExecutorService createThreadPool(final int acceptCount, final int maxThreads) {
        return new ThreadPoolExecutor(
                Math.min(DEFAULT_CORE_THREAD_COUNT, maxThreads),
                maxThreads,
                60,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(acceptCount),
                new AbortPolicy()
        );
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

    private void process(final Socket connection) throws IOException {
        if (connection == null) {
            return;
        }
        try {
            var processor = new Http11Processor(connection);
            threadPoolExecutor.execute(processor);

        } catch (RejectedExecutionException e) {
            log.error("Thread pool is full. request rejected: {}", e.getMessage());
            responseServerIsBusy(connection);
        }
    }

    private void responseServerIsBusy(final Socket connection) {
        try (final var os = connection.getOutputStream()) {
            final var message = "sorry, server is very busy. please request after few seconds.";
            HttpResponse.builder(HttpStatusCode.SERVICE_UNAVAILABLE)
                    .contentLength(message.getBytes().length)
                    .body(message)
                    .build()
                    .writeTo(os);
            os.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    public void stop() {
        stopped = true;
        threadPoolExecutor.shutdown();
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

    private int checkMaxThreads(final int maxThreads) {
        return Math.max(maxThreads, DEFAULT_MAX_THREAD_COUNT);
    }
}
