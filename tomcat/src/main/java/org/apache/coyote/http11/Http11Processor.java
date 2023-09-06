package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final HandlerMapping HANDLER_MAPPING = new HandlerMapping();

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
        try (
                final var inputStream = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream();
                final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            HttpRequest httpRequest = HttpRequestParser.extract(reader);

            String response = getResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest httpRequest) {
        if ("/".equals(httpRequest.getRequestLine().getPath())) {
            return new StringBuilder()
                    .append("HTTP/1.1 200 OK ").append("\r\n")
                    .append("Content-Type: text/html;charset=utf-8 ").append("\r\n")
                    .append("Content-Length: 12 ").append("\r\n")
                    .append("\r\n")
                    .append("Hello world!")
                    .toString();
        }

        HttpResponse httpResponse = HANDLER_MAPPING.extractHttpResponse(httpRequest);
        return httpResponse.extractResponse();
    }

}

