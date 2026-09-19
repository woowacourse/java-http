package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;

    private final ServerSocket serverSocket;
    private volatile boolean stopped;

    private Connector(final ServerSocket serverSocket, boolean stopped) {
        this.serverSocket = serverSocket;
        this.stopped = stopped;
    }

    public static Connector create() {
        try {
            return new Connector(
                    new ServerSocket(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT), false
            );
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void startListening() {
        Thread thread = new Thread(this);
        thread.start();
        stopped = false;

        log.info("Web Application Server started {} port.", serverSocket.getLocalPort());
    }

    @Override
    public void run() {
        while (!stopped) {
            acceptConnection();
        }
    }

    private void acceptConnection() {
        try {
            Socket connection = serverSocket.accept();
            dispatch(connection);
        } catch (IOException e) {
            if (stopped) {
                return;
            }

            log.error(e.getMessage(), e);
        }
    }

    private void dispatch(final Socket connection) {
        Http11Processor processor = new Http11Processor(connection);

        Thread thread = new Thread(processor);
        thread.start();
    }

    public void stopListening() {
        stopped = true;

        try {
            serverSocket.close();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
