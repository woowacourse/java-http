package nextstep.jwp;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.UUID;

public class FrontController implements Runnable {

    private static final Logger LOG = LoggerFactory.getLogger(FrontController.class);

    private final Socket connection;

    public FrontController(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest request = new HttpRequest(reader);
            HttpResponse response = new HttpResponse();
            if (!request.hasSessionId()) {
                response.setCookie(UUID.randomUUID().toString());
            }

            String uri = request.getPath();
            Controller controller = RequestMapping.getController(uri);
            controller.process(request, response);

            response.write(outputStream);
        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOG.error("Exception closing socket", exception);
        }
    }
}
