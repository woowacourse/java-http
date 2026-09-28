package org.apache.catalina.connector;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.Http11Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;
    private static final int DEFAULT_MAX_THREADS = 250;

    private final ServerSocket serverSocket;
    private final RequestHandler requestHandler;
    private final ExecutorService executorService;

    private volatile boolean stopped;

    private Connector(ServerSocket serverSocket, RequestHandler requestHandler, ExecutorService executorService, boolean stopped) {
        this.serverSocket = serverSocket;
        this.requestHandler = requestHandler;
        this.executorService = executorService;
        this.stopped = stopped;
    }

    public static Connector of(final RequestHandler requestHandler) {
        try {
            return new Connector(new ServerSocket(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT),
                    requestHandler, Executors.newFixedThreadPool(DEFAULT_MAX_THREADS), false);
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
        try {
            while (!stopped) {
                acceptConnection();
            }
        } finally {
            executorService.shutdown();
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
        Http11Processor processor = new Http11Processor(connection, requestHandler);
        executorService.execute(processor);
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
