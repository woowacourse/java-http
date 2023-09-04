package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
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
            ResponseEntity responseEntity = handlerMapping.extractResponseEntity(httpRequest.getRequestLine());

            ViewResolver viewResolver = new ViewResolver(responseEntity);

            HttpResponse httpResponse = viewResolver.extractHttpResponse();
            String response = httpResponse.extractResponse();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }


}

