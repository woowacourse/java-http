package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import org.apache.catalina.RequestMapping;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;

    private final ServerSocket serverSocket;
    private final RequestMapping requestMapping;
    private final ExecutorService executorService;

    private boolean stopped;

    public Connector(RequestMapping requestMapping, ExecutorService executorService) {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, requestMapping, executorService);
    }

    public Connector(final int port, final int acceptCount, RequestMapping requestMapping,
                     ExecutorService executorService) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.requestMapping = requestMapping;
        this.executorService = executorService;
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

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, requestMapping);
        executorService.submit(processor);
    }

    public void stop() {
        stopped = true;

        // 1. 새로운 요청을 받지 않도록 소켓 닫기
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error("Failed to close server socket", e);
        }

        // 2. ExecutorService에게 더 이상 새 작업을 받지 말라고 알림
        executorService.shutdown();

        // 3. 현재 진행 중인 작업이 완료될 때까지 대기
        try {
            if (!executorService.awaitTermination(60, TimeUnit.SECONDS)) {
                // 타임아웃 발생 시 강제 종료
                executorService.shutdownNow();
                log.warn("ExecutorService did not terminate in 60 seconds. Forcing shutdown.");
            }
        } catch (InterruptedException e) {
            // 대기 중 인터럽트 발생 시 강제 종료
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            log.error("Shutdown was interrupted. Forcing shutdown.", e);
        }

        log.info("Web Application Server stopped.");
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
