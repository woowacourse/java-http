package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.core.HandlerMap;
import nextstep.jwp.web.HttpRequest;
import nextstep.jwp.web.HttpRequestParser;
import nextstep.jwp.web.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

import static nextstep.jwp.core.HandlerMap.getController;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequestParser.parse(inputStream);
            HttpResponse response = new HttpResponse();

            Controller controller = getController(request);
            controller.service(request, response);

            outputStream.write(response.serialize().getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception exception) {
            log.error("Unexpected exception", exception);
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
