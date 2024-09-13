package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.coyote.ServletContainer;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREAD_COUNT = 250;

    private final ServerSocket serverSocket;
    private final ServletContainer servletContainer;
    private final ExecutorService executorService;

    private boolean stopped;

    public Connector(ServletContainer servletContainer) {
        this(servletContainer, DEFAULT_PORT, DEFAULT_ACCEPT_COUNT, DEFAULT_MAX_THREAD_COUNT);
    }

    public Connector(ServletContainer servletContainer, int port, int acceptCount, int maxThreads) {
        this.servletContainer = servletContainer;
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
        this.executorService = Executors.newFixedThreadPool(maxThreads);
    }

    private ServerSocket createServerSocket(int port, int acceptCount) {
        try {
            int checkedPort = checkPort(port);
            int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start() {
        executorService.submit(this);
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

    private void process(Socket connection) {
        if (connection == null) {
            return;
        }
        var processor = new Http11Processor(servletContainer, connection);
        new Thread(processor).start();
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private int checkPort(int port) {
        var MIN_PORT = 1;
        var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
