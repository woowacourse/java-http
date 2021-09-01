package nextstep.jwp.ui;

import nextstep.jwp.exception.HttpStatusException;
import nextstep.jwp.ui.controller.Controller;
import nextstep.jwp.ui.request.HttpRequest;
import nextstep.jwp.ui.response.HttpResponse;
import nextstep.jwp.ui.response.HttpStatus;
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
    private final RequestMapping requestMapping;

    public RequestHandler(Socket connection, RequestMapping requestMapping) {
        this.connection = Objects.requireNonNull(connection);
        this.requestMapping = requestMapping;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            HttpResponse response = new HttpResponse();
            doProcess(inputStream, response);

            String responseContent = response.getResponse();
            outputStream.write(responseContent.getBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void doProcess(InputStream inputStream, HttpResponse response) {

        try {
            HttpRequest request = new HttpRequest(inputStream);

            Controller controller = requestMapping.getController(request);
            controller.service(request, response);
        } catch (HttpStatusException e) {
            response.forward(e.getPath(), e.statusCode());
        } catch (RuntimeException e) {
            response.forward(HttpStatus.getPath(HttpStatus.INTERNAL_SERVER_ERROR), HttpStatus.INTERNAL_SERVER_ERROR);
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
