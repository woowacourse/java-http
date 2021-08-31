package nextstep.jwp.mvc;

import nextstep.jwp.request.CharlieHttpRequest;
import nextstep.jwp.request.HttpRequest;
import nextstep.jwp.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final FrontController frontController;

    public RequestHandler(Socket connection) {
        this.connection = Objects.requireNonNull(connection);
        this.frontController = FrontController.getInstance();
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            HttpRequest httpRequest = CharlieHttpRequest.of(bufferedReader);
            HttpResponse httpResponse = frontController.getResponse(httpRequest);

            String response = httpResponse.toHttpResponseMessage();
            outputStream.write(response.getBytes());
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
