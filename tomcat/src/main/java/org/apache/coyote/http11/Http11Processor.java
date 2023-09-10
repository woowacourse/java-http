package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.handler.Controller;
import org.apache.coyote.handler.FrontController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String EXTENSION_DELIMITER = ".";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HttpRequestParser httpRequestParser = new HttpRequestParser();
    private static final HttpResponseGenerator httpResponseGenerator = new HttpResponseGenerator();

    private final Socket connection;
    private final FrontController frontController;

    public Http11Processor(final Socket connection, final FrontController frontController) {
        this.connection = connection;
        this.frontController = frontController;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final HttpRequest httpRequest = httpRequestParser.parse(bufferedReader);
            final HttpResponse httpResponse = new HttpResponse();

            doService(httpRequest, httpResponse);

            setGeneralResponse(httpRequest, httpResponse);
            final String response = httpResponseGenerator.generate(httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doService(final HttpRequest request, final HttpResponse response) {
        final String requestUri = request.getRequestUri();

        if (isFileRequest(requestUri)) {
            readFile(requestUri, response);
            return;
        }
        final Controller controller = frontController.getHandler(requestUri);
        controller.service(request, response);
    }

    private boolean isFileRequest(final String requestUri) {
        return requestUri.contains(EXTENSION_DELIMITER);
    }

    private void readFile(final String requestUri, final HttpResponse response) {
        final String responseBody = ViewResolver.read(requestUri);
        response.setResponseBody(responseBody);
        response.setStatusCode(HttpStatusCode.OK);
    }

    private void setGeneralResponse(final HttpRequest request, final HttpResponse response) {
        response.setProtocol(request.getProtocol());
        response.setHeader(HttpHeaders.CONTENT_TYPE, request.getContentType());
        response.setHeader(HttpHeaders.CONTENT_LENGTH, String.valueOf(response.getContentLength()));
    }
}
