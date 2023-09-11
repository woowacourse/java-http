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

            httpResponse.setProtocol(httpRequest.getProtocol());
            httpResponse.setHeader(HttpHeaders.CONTENT_TYPE, httpRequest.getContentType());
            final String response = httpResponseGenerator.generate(httpResponse);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void doService(final HttpRequest request, final HttpResponse response) {
        final String requestUri = request.getRequestUri();

        final Controller controller = frontController.getHandler(requestUri);
        controller.service(request, response);
    }
}
