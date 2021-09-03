package nextstep.jwp.server;

import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final ControllerDispatcher controllerDispatcher;

    public RequestHandler(Socket connection, ControllerDispatcher controllerDispatcher) {
        this.connection = Objects.requireNonNull(connection);
        this.controllerDispatcher = controllerDispatcher;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.of(inputStream);
            HttpResponse httpResponse = new HttpResponse(outputStream);
            controllerDispatcher.execute(httpRequest, httpResponse);
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
