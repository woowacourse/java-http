package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.catalina.container.Container;
import org.apache.catalina.server.ApplicationConfig;
import org.apache.catalina.server.Context;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final Long KEEP_ALIVE_TIME = 60L;

    private final Container container;
    private final ServerSocket serverSocket;
    private final ThreadPoolExecutor threadPoolExecutor;
    private final ApplicationConfig applicationConfig;
    private boolean stopped;

    public Connector(final Context context) {
        this.applicationConfig = context.getApplicationConfig();
        this.container = context.getContainer();
        this.serverSocket = createServerSocket(applicationConfig.getPort(), applicationConfig.getAcceptCount());
        this.threadPoolExecutor = createExecutorService(applicationConfig.getMaxThreads());
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

    private ThreadPoolExecutor createExecutorService(final int maxThreads) {
        return new ThreadPoolExecutor(
                maxThreads,
                maxThreads,
                KEEP_ALIVE_TIME,
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(DEFAULT_ACCEPT_COUNT),
                new ThreadPoolExecutor.AbortPolicy()
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
            Socket connection = serverSocket.accept();
            threadPoolExecutor.submit(() -> process(connection));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, container);
        new Thread(processor).start();
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

    public int getActiveRequestCount() {
        return threadPoolExecutor.getActiveCount();
    }

    public int getPendingRequestCount() {
        return threadPoolExecutor.getQueue().size();
    }

    public int getCompletedRequestCount() {
        return (int) threadPoolExecutor.getCompletedTaskCount();
    }
}
