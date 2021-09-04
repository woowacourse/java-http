package nextstep.jwp;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

import nextstep.jwp.controller.Controller;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    public static final Logger LOG = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
    }

    @Override
    public void run() {
        LOG.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (
                final InputStream inputStream = connection.getInputStream();
                final OutputStream outputStream = connection.getOutputStream();
                final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        ) {

            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse(outputStream);

            Controller controller = RequestMapper.getController(httpRequest.getPath());
            controller.process(httpRequest, httpResponse);

        } catch (IOException exception) {
            LOG.error("Exception stream", exception);
        } catch (IllegalArgumentException e) {
            LOG.error(e.getMessage());
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
