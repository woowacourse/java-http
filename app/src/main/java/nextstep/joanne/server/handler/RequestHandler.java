package nextstep.joanne.server.handler;

import nextstep.joanne.dashboard.exception.HttpException;
import nextstep.joanne.server.handler.controller.Controller;
import nextstep.joanne.server.handler.controller.ControllerAdvice;
import nextstep.joanne.server.http.request.HttpRequest;
import nextstep.joanne.server.http.request.HttpRequestParser;
import nextstep.joanne.server.http.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Objects;

public class RequestHandler implements Runnable {

    private static final Logger log = LoggerFactory.getLogger(RequestHandler.class);

    private final Socket connection;
    private final HandlerMapping handlerMapping;
    private final HttpRequestParser httpRequestParser;

    public RequestHandler(Socket connection, HandlerMapping handlerMapping, HttpRequestParser httpRequestParser) {
        this.connection = Objects.requireNonNull(connection);
        this.handlerMapping = handlerMapping;
        this.httpRequestParser = httpRequestParser;
    }

    @Override
    public void run() {
        log.debug("New Client Connect! Connected IP : {}, Port : {}", connection.getInetAddress(), connection.getPort());

        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream()) {
            final BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

            final HttpRequest httpRequest = httpRequestParser.parse(br);
            HttpResponse httpResponse = new HttpResponse();
            final Controller controller = handlerMapping.get(httpRequest);

            try {
                controller.service(httpRequest, httpResponse);
            } catch (HttpException e) {
                ControllerAdvice.handle(httpRequest, httpResponse, e.httpStatus());
            }

            outputStream.write(httpResponse.getBody().getBytes());
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
