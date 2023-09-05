package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.MimeType;
import org.apache.coyote.http11.common.HttpMethod;
import org.apache.coyote.http11.common.HttpStatus;
import org.apache.coyote.http11.handler.HandlerMapping;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
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
        try (
                final var inputStream = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream();
                final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))
        ) {
            HttpRequest httpRequest = HttpRequestParser.extract(reader);

            HandlerMapping handlerMapping = new HandlerMapping();
            ResponseEntity responseEntity = handlerMapping.extractResponseEntity(httpRequest);

            String response = extractResponse(responseEntity);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractResponse(ResponseEntity responseEntity) {
        if (responseEntity.hasSameHttpMethod(HttpMethod.GET)) {
            ViewResolver viewResolver = new ViewResolver(responseEntity);
            HttpResponse httpResponse = viewResolver.extractHttpResponse();

            return httpResponse.extractResponse();
        }
        return redirect(responseEntity);
    }

    private String redirect(ResponseEntity responseEntity) {
        HttpStatus httpStatus = responseEntity.getHttpStatus();

        return new StringBuilder()
                .append(String.format("HTTP/1.1 %s %s ", httpStatus.getStatusCode(), httpStatus.name())).append("\r\n")
                .append(String.format("Content-Type: %s;charset=utf-8 ", MimeType.HTML)).append("\r\n")
                .append(String.format("Location: %s.html", responseEntity.getPath())).append("\r\n")
                .append("\r\n")
                .toString();
    }


}

