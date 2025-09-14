package org.apache.coyote.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import org.apache.catalina.ProcessBroker;
import org.apache.catalina.servlet.ServletMapper;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int ACCEPTOR_THREAD_COUNT = 1;
    private static final int POLLER_THREAD_COUNT = Runtime.getRuntime().availableProcessors();
    private static final int MAX_WORKER_THREAD_COUNT = 200;

    private final ServerSocket serverSocket;
    private boolean stopped;
    private final ExecutorService acceptorExecutor;
    private final ExecutorService pollerExecutor;
    private final ExecutorService workerExecutor;


    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, ACCEPTOR_THREAD_COUNT, POLLER_THREAD_COUNT, MAX_WORKER_THREAD_COUNT);
    }

    public Connector(final int port, final int acceptCount, final int acceptorThreadCount, final int pollerThreadCount, final int maxWorkerThreadCount) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        ThreadFactory daemonFactory = craeteDaemonThreadFactory();
        this.acceptorExecutor = Executors.newFixedThreadPool(acceptorThreadCount, daemonFactory);
        this.pollerExecutor = Executors.newFixedThreadPool(pollerThreadCount, daemonFactory);
        this.workerExecutor = Executors.newFixedThreadPool(maxWorkerThreadCount, daemonFactory);
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
        acceptorExecutor.submit(this);
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
            Socket socket = serverSocket.accept();
            pollerExecutor.submit(() -> process(socket));
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(connection, new ProcessBroker(new ServletMapper()));
        workerExecutor.submit(processor);
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
            acceptorExecutor.shutdownNow();
            pollerExecutor.shutdownNow();
            workerExecutor.shutdownNow();
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

    private static ThreadFactory craeteDaemonThreadFactory() {
        return runnable -> {
            Thread t = new Thread(runnable);
            t.setDaemon(true);
            t.setName("MyDaemon-" + System.currentTimeMillis());
            return t;
        };
    }
}
