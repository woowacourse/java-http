package nextstep.jwp.server;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;
import nextstep.jwp.controller.Controllers;
import nextstep.jwp.exception.InvalidHttpRequestException;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RequestHandler implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final Controllers controllers;

    public RequestHandler(Socket connection, Controllers controllers) {
        this.connection = Objects.requireNonNull(connection);
        this.controllers = controllers;
    }

    @Override
    public void run() {
        LOGGER.debug("New Client Connect! Connected IP : {}, Port : {}",
            connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
            final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.parse(inputStream);
            LOGGER.info("Parsed HTTP Request!!!\n\n{}\n\n", httpRequest);
            HttpResponse httpResponse = controllers.doService(httpRequest);
            LOGGER.info("Return HTTP Response!!!\n\n{}\n\n", httpResponse);

            flushBytes(outputStream, httpResponse);
        } catch (IOException | InvalidHttpRequestException exception) {
            LOGGER.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void flushBytes(OutputStream outputStream, HttpResponse httpResponse)
        throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        bufferedOutputStream.write(httpResponse.toBytes());
        bufferedOutputStream.flush();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            LOGGER.error("Exception closing socket", exception);
        }
    }
}
