package org.apache.coyote.http11;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.RequestHandler;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestHandler requestHandler;

    public Http11Processor(final Socket connection, final RequestHandler requestHandler) {
        this.connection = connection;
        this.requestHandler = requestHandler;
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final Optional<HttpRequest> parsed = HttpRequest.from(bufferedReader);
            if (parsed.isEmpty()) {
                return;
            }
            final HttpRequest request = parsed.get();
            final HttpResponse response = new HttpResponse();
            request.bind(response);

            requestHandler.service(request, response);

            outputStream.write(response.toMessage().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }
}
