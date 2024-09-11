package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
import org.apache.coyote.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UncheckedServletException;

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

            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));

            final var httpRequest = HttpRequest.from(bufferedReader);
            log.info("요청 = {}", httpRequest.getRequestLine());
            final var httpResponse = new HttpResponse();
            final var requestMapping = new RequestMapping();
            final var controller = requestMapping.getController(httpRequest);
            controller.service(httpRequest, httpResponse);

            outputStream.write(httpResponse.toHttpResponse().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
