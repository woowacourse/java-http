package nextstep.jwp;

import nextstep.jwp.exception.NotSupportedMethodException;
import nextstep.jwp.http.controller.Controller;
import nextstep.jwp.http.controller.RequestMapping;
import nextstep.jwp.http.http_request.JwpHttpRequest;
import nextstep.jwp.http.http_response.JwpHttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Objects;

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
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            JwpHttpResponse response = handleRequest(reader);
            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private JwpHttpResponse handleRequest(BufferedReader reader) {
        try {
            JwpHttpRequest request = new JwpHttpRequest(reader);
            Controller handler = RequestMapping.getController(request.getUri());
            return handler.handle(request);
        } catch (IOException | URISyntaxException | NotSupportedMethodException e) {
            log.error(e.getMessage());
            return JwpHttpResponse.notFound();
        } catch (Exception e) {
            log.error("RuntimeException: {}", e.getMessage());
            return JwpHttpResponse.internalServerError();
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
