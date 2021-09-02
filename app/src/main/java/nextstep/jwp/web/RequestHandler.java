package nextstep.jwp.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.exception.HttpException;
import nextstep.jwp.web.handler.HandlerMapping;
import nextstep.jwp.web.handler.HttpExceptionHandler;
import nextstep.jwp.web.handler.HttpRequestHandler;
import nextstep.jwp.web.http.request.HttpRequest;
import nextstep.jwp.web.http.response.HttpResponse;
import nextstep.jwp.web.http.response.StatusCode;
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
        log.debug("New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(),
            connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream();
            final BufferedReader bufferedReader =
                new BufferedReader(new InputStreamReader(inputStream))
        ) {

            HttpRequest httpRequest = new HttpRequest(bufferedReader);
            HttpResponse httpResponse = new HttpResponse();

            handleRequest(httpRequest, httpResponse);

            outputStream.write(httpResponse.asString().getBytes());
            outputStream.flush();

        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } catch (RuntimeException exception) {
            log.error("Exception runtime", exception);
        } finally {
            close();
        }
    }

    private void handleRequest(HttpRequest request, HttpResponse response) throws IOException {
        try {
            HttpRequestHandler mappedHandler =
                HandlerMapping.getHandler(request);
            mappedHandler.handle(request, response);
        } catch (HttpException exception) {
            log.error("HttpException Handling Request", exception);
            new HttpExceptionHandler().handle(exception.getStatusCode(), response);
        } catch (RuntimeException exception) {
            log.error("RuntimeException Handling Request", exception);
            new HttpExceptionHandler().handle(StatusCode.INTERNAL_SERVER_ERROR, response);
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
