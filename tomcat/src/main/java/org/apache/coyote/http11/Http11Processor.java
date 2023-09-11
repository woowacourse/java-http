package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.FrontController;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.parser.HttpRequestMessageReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.formatter.HttpResponseMessageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var br = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequest httpRequest = HttpRequestMessageReader.readHttpRequest(br);
            final HttpResponse httpResponse = new HttpResponse();
            FrontController.handleHttpRequest(httpRequest, httpResponse);
            HttpResponseMessageWriter.writeHttpResponse(httpResponse, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
