package nextstep.jwp.server;

import nextstep.jwp.server.handler.RequestHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    private final ServerSocket serverSocket;

    public WebServer(ServerSocket serverSocket) {
        this.serverSocket = serverSocket;
    }

    public void run() {
        try (serverSocket) {
            logger.info("Web Server started {} port.", serverSocket.getLocalPort());
            handle(serverSocket);
        } catch (IOException exception) {
            logger.error("Exception accepting connection", exception);
        } catch (RuntimeException exception) {
            logger.error("Unexpected error", exception);
        }
    }

    private void handle(ServerSocket serverSocket) throws IOException {
        Socket connection;
        while ((connection = serverSocket.accept()).isConnected()) {
            new Thread(new RequestHandler(connection)).start();
        }
    }

    public static int defaultPortIfNull(String[] args) {
        int port = Stream.of(args)
            .filter(WebServer::isNumeric)
            .map(Integer::parseInt)
            .findFirst()
            .orElse(WebServer.DEFAULT_PORT);

        return checkPort(port);
    }

    private static int checkPort(int port) {
        if (port < 1 || 65535 < port) {
            return DEFAULT_PORT;
        }
        return port;
    }

    private static boolean isNumeric(String input) {
        return input.chars()
            .allMatch(Character::isDigit);
    }
}
