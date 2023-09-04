package org.apache.catalina.connector;

import nextstep.jwp.HandlerResolver;
import nextstep.jwp.JwpHttpDispatcher;
import nextstep.jwp.handler.get.LoginGetHandler;
import nextstep.jwp.handler.get.RegisterGetHandler;
import nextstep.jwp.handler.get.RootGetHandler;
import nextstep.jwp.handler.post.LoginPostHandler;
import nextstep.jwp.handler.post.RegisterPostHandler;
import org.apache.coyote.http11.Handler;
import org.apache.coyote.http11.Http11Processor;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;

public class Connector implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(Connector.class);

    private static final int DEFAULT_PORT = 8080;
    private static final int DEFAULT_ACCEPT_COUNT = 100;

    private final Map<String, Handler> httpGetHandlers =
            Map.of("/", new RootGetHandler(),
                    "/login", new LoginGetHandler(new SessionManager()),
                    "/register", new RegisterGetHandler());
    private final Map<String, Handler> httpPostHandlers =
            Map.of("/login", new LoginPostHandler(new SessionManager()),
                    "/register", new RegisterPostHandler());

    private final ServerSocket serverSocket;
    private boolean stopped;

    public Connector() {
        this(DEFAULT_PORT, DEFAULT_ACCEPT_COUNT);
    }

    public Connector(final int port, final int acceptCount) {
        this.serverSocket = createServerSocket(port, acceptCount);
        this.stopped = false;
    }

    private ServerSocket createServerSocket(final int port, final int acceptCount) {
        try {
            final int checkedPort = checkPort(port);
            final int checkedAcceptCount = checkAcceptCount(acceptCount);
            return new ServerSocket(checkedPort, checkedAcceptCount);
        } catch (final IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    public void start() {
        final var thread = new Thread(this);
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
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void process(final Socket connection) {
        if (connection == null) {
            return;
        }
        final JwpHttpDispatcher httpDispatcher = new JwpHttpDispatcher(new HandlerResolver(httpGetHandlers, httpPostHandlers));
        final var processor = new Http11Processor(connection, new HttpRequestParser(), httpDispatcher);
        new Thread(processor).start();
    }

    public void stop() {
        stopped = true;
        try {
            serverSocket.close();
        } catch (final IOException e) {
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
}
