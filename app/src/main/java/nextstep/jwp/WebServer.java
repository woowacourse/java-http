package nextstep.jwp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger LOGGER = LoggerFactory.getLogger(WebServer.class);
    private static final int DEFAULT_PORT = 8080;

    private final int port;

    public WebServer(int port) {
        this.port = checkPort(port);
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            LOGGER.info("Web Server started {} port.", serverSocket.getLocalPort());
            handle(serverSocket);
        } catch (IOException exception) {
            LOGGER.error("Exception accepting connection", exception);
        } catch (RuntimeException exception) {
            LOGGER.error("Unexpected error", exception);
        }
    }

    private void handle(ServerSocket serverSocket) throws IOException {
        Socket connection;
        RequestMapping requestMapping = new RequestMapping();
        while ((connection = serverSocket.accept()) != null) {
            new Thread(new RequestHandler(connection, requestMapping)).start();
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
