package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.coyote.Dispatcher;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;

    private final ServerSocket serverSocket;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT);
    }

    public Connector(int port, int acceptCount) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(int port, int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start(Dispatcher dispatcher) {
        Thread thread = new Thread(() -> run(dispatcher));
        thread.setDaemon(true);
        thread.start();
        stopped = false;
        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    public void run(Dispatcher dispatcher) {
        // 클라이언트가 연결될때까지 대기한다.
        while (!stopped) {
            connect(dispatcher);
        }
    }

    private void connect(Dispatcher dispatcher) {
        try {
            process(serverSocket.accept(), dispatcher);
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(Socket connection, Dispatcher dispatcher) {
        if (connection == null) {
            return;
        }
        Http11Processor processor = new Http11Processor(connection, dispatcher);
        Thread thread = new Thread(processor);
        log.info("new thread created: {}", thread.threadId());
        thread.start();
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
        final var MIN_PORT = 1;
        final var MAX_PORT = 65535;

        if (port < MIN_PORT || MAX_PORT < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private int checkAcceptCount(int acceptCount) {
        return Math.max(acceptCount, DEFAULT_ACCEPT_COUNT);
    }
}
