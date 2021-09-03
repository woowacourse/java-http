package nextstep.jwp.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.RequestMapper;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);
    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(),
                connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest httpRequest = HttpRequest.createFromInputStream(inputStream);

            if (httpRequest.isEmpty()) {
                return;
            }

            RequestMapper requestMapper = new RequestMapper();
            Controller matchedController = requestMapper.getController(httpRequest);
            HttpResponse response = matchedController.service(httpRequest);

            outputStream.write(response.getResponseToString().getBytes());
            outputStream.flush();

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception exception) {
            log.error("Error", exception);
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
