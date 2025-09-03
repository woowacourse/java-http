package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.handler.HandlerExecutor;
import org.apache.coyote.http11.message.request.HttpRequest;
import org.apache.coyote.http11.message.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
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
        try (var reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
             var writer = connection.getOutputStream()) {

            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            HttpRequest request = HttpRequest.from(requestLine);
            HttpResponse response = handlerExecutor.execute(request);

            writer.write(response.toText().getBytes());
            writer.flush();
        } catch (Exception e) {
            log.error("Processor 처리 중 예외 발생", e);
        } finally {
            try {
                connection.close();
            } catch (IOException ignored) {
            }
        }

    }
}
