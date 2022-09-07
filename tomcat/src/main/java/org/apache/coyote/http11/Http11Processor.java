package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import nextstep.jwp.controller.Controller;
import nextstep.jwp.controller.ControllerMapping;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.model.request.HttpRequest;
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
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {

            HttpRequest httpRequest = HttpRequest.from(reader);
            final var response = createResponse(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final HttpRequest httpRequest) {
        Controller handler = ControllerMapping.findController(httpRequest);
        return handler.service(httpRequest)
                .getResponse();
    }
}
