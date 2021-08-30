package nextstep.jwp.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Stream;

public class WebServer {

    private static final Logger log = LoggerFactory.getLogger(WebServer.class);

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    public WebServer(int port) {
        this.port = checkPort(port);
    }

    private int checkPort(int port) {
        if (port < 1 || 65535 < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            log.info("Web Server started at {} port.", serverSocket.getLocalPort());
            handle(serverSocket);
        } catch (IOException exception) {
            log.error("Exception accepting connection", exception);
        } catch (RuntimeException exception) {
            log.error("Unexpected error", exception);
        }
    }

    private void handle(ServerSocket serverSocket) throws IOException {
        final ExecutorService executorService = Executors.newFixedThreadPool(50);
        Socket connection;
        while ((connection = serverSocket.accept()) != null) {
            executorService.submit(new RequestHandler(connection));
        }
    }

    public static int defaultPortIfNull(String[] args) {
        return Stream.of(args)
                .findFirst()
                .map(Integer::parseInt)
                .orElse(WebServer.DEFAULT_PORT);
    }
}
