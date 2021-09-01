package nextstep.jwp.framework;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.framework.http.HttpRequest;
import nextstep.jwp.framework.http.HttpResponse;
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
        // log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {
            final RequestMapping requestMapping = RequestMapping.create();
            final HttpRequest request = new HttpRequest(inputStream);
            final HttpResponse response = new HttpResponse();
            final Controller controller = requestMapping.getController(request);

            controller.service(request, response);
            doOutputStream(outputStream, response);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            close();
        }
    }

    private void doOutputStream(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
