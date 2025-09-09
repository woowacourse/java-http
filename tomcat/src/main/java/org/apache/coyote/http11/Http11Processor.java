package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.httpRequest.HttpRequest;
import org.apache.coyote.http11.httpRequest.RequestParser;
import org.apache.coyote.http11.httpResponse.HttpResponse;
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
        try (final var inputStreamReader = new InputStreamReader(connection.getInputStream());
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = RequestParser.parse(bufferedReader);
            final HttpResponse httpResponse = HttpResponse.build(httpRequest);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }
}
