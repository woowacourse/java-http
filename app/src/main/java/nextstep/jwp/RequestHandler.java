package nextstep.jwp;

import nextstep.jwp.controller.*;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Map<String, Controller> controllerMap = new HashMap<>();

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        controllerMap.put("/", new IndexController());
        controllerMap.put("/login", new LoginController());
        controllerMap.put("/register", new RegisterController());
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(reader);
            HttpResponse response = new HttpResponse();

            String uri = request.getUri();
            Controller controller = controllerMap.getOrDefault(uri, new DefaultController());
            controller.process(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
