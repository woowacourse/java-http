package nextstep.jwp.server;

import java.io.BufferedOutputStream;
import nextstep.jwp.controller.Controllers;
import nextstep.jwp.controller.StaticResourceController;
import nextstep.jwp.http.request.HttpRequest;
import nextstep.jwp.http.response.HttpResponse;
import nextstep.jwp.service.StaticResourceService;
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
    private final Controllers controllers;

    public RequestHandler(Socket connection, Controllers controllers) {
        this.connection = Objects.requireNonNull(connection);
        this.controllers = controllers;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {

            HttpRequest httpRequest = HttpRequest.parse(inputStream);
            HttpResponse httpResponse = controllers.doService(httpRequest);

            flushBytes(outputStream, httpResponse);
        } catch (IOException exception) {
            log.error("Exception stream", exception);
        } finally {
            close();
        }
    }

    private void flushBytes(OutputStream outputStream, HttpResponse httpResponse) throws IOException {
        BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(outputStream);
        bufferedOutputStream.write(httpResponse.toBytes());
        bufferedOutputStream.flush();
    }

    private void close() {
        try {
            connection.close();
        } catch (IOException exception) {
            log.error("Exception closing socket", exception);
        }
    }
}
