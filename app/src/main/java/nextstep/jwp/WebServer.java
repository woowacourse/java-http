package nextstep.jwp;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.stream.Stream;
import nextstep.jwp.context.ApplicationContext;
import nextstep.jwp.context.ApplicationContextImpl;
import nextstep.jwp.dispatcher.adapter.HandlerAdapter;
import nextstep.jwp.dispatcher.adapter.HandlerAdapterFactory;
import nextstep.jwp.dispatcher.mapping.HandlerMapping;
import nextstep.jwp.dispatcher.mapping.HandlerMappingFactory;
import nextstep.project.presentation.HelloWorldController;
import nextstep.project.presentation.IndexController;
import nextstep.project.presentation.RegisterController;
import nextstep.project.presentation.LoginController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WebServer {

    private static final Logger logger = LoggerFactory.getLogger(WebServer.class);

    private static final int DEFAULT_PORT = 8080;

    private final int port;

    private ApplicationContext applicationContext;

    private List<HandlerMapping> handlerMappings;

    private List<HandlerAdapter> handlerAdapters;

    public WebServer(int port) {
        this.port = checkPort(port);
        this.initApplicationContext();
        this.handlerMappings = HandlerMappingFactory.createAllHandlerMapping();
        this.handlerAdapters = HandlerAdapterFactory.createAllHandlerAdapter();
    }

    private void initApplicationContext() {
        applicationContext = new ApplicationContextImpl();
        applicationContext.addHandler("/", new HelloWorldController());
        applicationContext.addHandler("/index", new IndexController());
        applicationContext.addHandler("/login", new LoginController());
        applicationContext.addHandler("/register", new RegisterController());
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
            new Thread(new RequestHandler(
                connection,
                applicationContext,
                handlerMappings,
                handlerAdapters
            )).start();
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
