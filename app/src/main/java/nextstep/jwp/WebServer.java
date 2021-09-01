package nextstep.jwp;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static final int DEFAULT_PORT = ServerConfig.DEFAULT_PORT;

    private final int port;
    private final Assembler container;

    public WebServer(int port, Assembler container) {
        this.port = checkPort(port);
        this.container = container;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Web Server started {} port.", serverSocket.getLocalPort());
            handle(serverSocket, container.dispatcher());
        } catch (IOException exception) {
            logger.error("Exception accepting connection", exception);
        } catch (RuntimeException exception) {
            logger.error("Unexpected error", exception);
        }
    }

    private void handle(ServerSocket serverSocket, Dispatcher dispatcher) throws IOException {
        Socket connection;
        while ((connection = serverSocket.accept()) != null) {
            Thread thread = new Thread(new RequestHandler(connection, dispatcher));
            thread.start();
        }
    }

    public static int defaultPortIfNull(String[] args) {
        return Stream.of(args)
                .findFirst()
                .map(Integer::parseInt)
                .orElse(WebServer.DEFAULT_PORT);
    }

    private int checkPort(int port) {
        if (port < 1 || 65535 < port) {
            return DEFAULT_PORT;
        }
        return port;
    }
}
