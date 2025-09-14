package org.apache.catalina.connector;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
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
    private static final int DEFAULT_MAX_THREADS = 10;
    private static final int DEFAULT_BACKLOG_COUNT = 200;

    private final ServerSocket serverSocket;
    private final ExecutorService executorService;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREADS);
    }

    public Connector(final int port, final int acceptCount, final int maxThreads) {
        this.serverSocket = createServerSocket(port, (acceptCount + maxThreads) * 2);
        this.executorService = new ThreadPoolExecutor(
                maxThreads / 2,
                maxThreads,
                60L,
                TimeUnit.SECONDS,
                new ArrayBlockingQueue<>(acceptCount),
                new ThreadPoolExecutor.AbortPolicy());
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int backlogCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkBacklogCount(backlogCount);
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

    private void process(final Socket connection) throws IOException {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection);
        try {
            executorService.submit(processor);
        } catch (RejectedExecutionException exception) {
            handleRejectedExecutionException(connection);
        }
    }

    private void handleRejectedExecutionException(Socket connection) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(connection.getOutputStream(), StandardCharsets.UTF_8))) {
            String body = """
                    {
                        "message": "서버가 현재 요청을 처리할 수 없습니다. 잠시 후 다시 시도해주세요."
                    }
                    """;
            writer.write("HTTP/1.1 429 Too Many Requests\r\n");
            writer.write("Content-Type: application/json\r\n");
            writer.write("Connection: close");
            writer.write("Content-Length: " + body.getBytes(StandardCharsets.UTF_8).length + "\r\n");
            writer.write("\r\n");
            writer.write(body);
            writer.flush();
            writer.close();
            connection.close();

            log.warn("Task rejected from ThreadPoolExecutor: maximum pool size reached");
        }
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

    private int checkBacklogCount(final int backlogCount) {
        return Math.max(backlogCount, DEFAULT_BACKLOG_COUNT);
    }
}
