package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

import org.apache.coyote.Processor;
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
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);

            HttpResponse httpResponse;
            try {
                httpResponse = ResponseGenerator.generate(httpRequest);
            } catch (Exception e) {
                log.error(e.getMessage());
                String body = StaticResourceReader.read("/500.html");
                httpResponse = HttpResponse.of(
                        httpRequest.getVersion(),
                        HttpStatus.INTERNATIONAL_SERVER_ERROR,
                        ContentType.HTML,
                        body
                );
            }

            outputStream.write(httpResponse.toHttpMessage().getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
