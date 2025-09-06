package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.exception.ExceptionHandler;
import org.apache.coyote.http11.handler.HandlerExecutor;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ExceptionHandler exceptionHandler = new ExceptionHandler();
    private final HandlerExecutor handlerExecutor = new HandlerExecutor();

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
        HttpResponse response;
        try (
                connection;
                var reader = new BufferedReader(
                        new InputStreamReader(connection.getInputStream(), StandardCharsets.ISO_8859_1));
                var writer = connection.getOutputStream()
        ) {
            try {
                String requestLine = reader.readLine();
                if (requestLine == null || requestLine.isBlank()) {
                    return;
                }

                HttpRequest request = HttpRequest.from(requestLine);
                response = handlerExecutor.execute(request);
            } catch (Exception e) {
                response = exceptionHandler.handle(e);
            }

            response.writeTo(writer);
            writer.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
