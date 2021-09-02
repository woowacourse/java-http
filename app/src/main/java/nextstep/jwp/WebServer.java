package nextstep.jwp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.stream.Stream;
import nextstep.jwp.mvc.DispatcherServlet;
import nextstep.jwp.webserver.request.HttpSessions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static final int DEFAULT_PORT = 8080;

    private final int port;
    private final DispatcherServlet dispatcherServlet;
    private final HttpSessions httpSessions;

    public WebServer(int port, DispatcherServlet dispatcherServlet,
            HttpSessions httpSessions) {
        this.port = checkPort(port);
        this.dispatcherServlet = dispatcherServlet;
        this.httpSessions = httpSessions;
    }

    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
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
        while ((connection = serverSocket.accept()) != null) {
            new Thread(new RequestHandler(connection, dispatcherServlet, httpSessions)).start();
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
