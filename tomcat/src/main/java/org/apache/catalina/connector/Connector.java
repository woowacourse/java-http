package org.apache.catalina.connector;

import org.apache.catalina.Manager;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_THREADS = 200;
    private static final int SHUTDOWN_TIMEOUT_SECONDS = 5;

    private final ServerSocket serverSocket;
    private final Manager sessionManager;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final CoyoteProcessorFactory processorFactory;
    private final int acceptCount;

    private boolean stopped;

    public Connector(Manager sessionManager) {
        this(
                DEFAULT_PORT,
                DEFAULT_ACCEPT_COUNT,
                DEFAULT_THREADS,
                sessionManager,
                new CoyoteProcessorFactory()
        );
    }

    public Connector(
            final int port,
            final int acceptCount,
            final int maxThreads,
            final Manager sessionManager,
            final CoyoteProcessorFactory processorFactory
    ) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.threadPoolExecutor = new TomcatThreadPool(maxThreads, acceptCount);
        this.sessionManager = sessionManager;
        this.processorFactory = processorFactory;
        this.acceptCount = acceptCount;
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
            // acceptCount 미만인 경우 연결을 accept한다.
            int nowQueueSize = threadPoolExecutor.getQueue().size();
            if (nowQueueSize < acceptCount) {
                process(serverSocket.accept());
            }
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) throws IOException {
        if (connection == null) {
            return;
        }
        Http11Processor processor = processorFactory.getHttp11Processor(connection, sessionManager);
        threadPoolExecutor.execute(processor);
    }

    public void stop() {
        stopped = true;
        try {
            threadPoolExecutor.shutdown();
            threadPoolExecutor.awaitTermination(SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            threadPoolExecutor.shutdownNow();
            serverSocket.close();
        } catch (IOException | InterruptedException e) {
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

    public int getActiveConnect() {
        return threadPoolExecutor.getActiveCount();
    }

    public int getWaitConnect() {
        return threadPoolExecutor.getQueue().size();
    }
}
