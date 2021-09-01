package nextstep.joanne;

import nextstep.joanne.handler.HandlerMapping;
import nextstep.joanne.handler.RequestHandler;
import nextstep.joanne.handler.RequestHandler2;
import nextstep.joanne.handler.controller.ControllerFactory;
import nextstep.joanne.http.request.HttpRequestParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;

public class WebServer2 {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    public WebServer2(int port) {
        this.port = checkPort(port);
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            logger.info("Web Server started {} port.", serverSocket.getLocalPort());

            HandlerMapping handlerMapping = new HandlerMapping(ControllerFactory.addControllers());
            HttpRequestParser httpRequestParser = new HttpRequestParser();

            handle(serverSocket, handlerMapping, httpRequestParser);
        } catch (IOException exception) {
            logger.error("Exception accepting connection", exception);
        } catch (RuntimeException exception) {
            logger.error("Unexpected error", exception);
        }
    }

    private void handle(ServerSocket serverSocket, HandlerMapping handlerMapping, HttpRequestParser httpRequestParser)
            throws IOException {
        Socket connection;
        while ((connection = serverSocket.accept()) != null) {
            new Thread(new RequestHandler2(connection, handlerMapping, httpRequestParser)).start();
        }
    }

    public static int defaultPortIfNull(String[] args) {
        return Stream.of(args)
                .findFirst()
                .map(Integer::parseInt)
                .orElse(WebServer2.DEFAULT_PORT);
    }

    private int checkPort(int port) {
        if (port < 1 || 65535 < port) {
            return DEFAULT_PORT;
        }
        return port;
    }
}
