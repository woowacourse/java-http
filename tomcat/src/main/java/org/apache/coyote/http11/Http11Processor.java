package org.apache.coyote.http11;

import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.Handlers;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.RequestFirstLine;
import org.apache.coyote.http11.http.RequestHeader;
import org.apache.coyote.http11.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

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
             final var outputStream = connection.getOutputStream()) {

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            String httpRequestText = br.readLine();
            RequestFirstLine requestLine = RequestFirstLine.from(httpRequestText);
            log.info(requestLine.getRequestUri());
            RequestHeader requestHeader = RequestHeader.from(br);

            HttpRequest httpRequest = new HttpRequest(requestLine, requestHeader);
            ResponseEntity responseEntity = Handlers.handle(httpRequest);
            String response = responseEntity.generateResponseMessage();

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
