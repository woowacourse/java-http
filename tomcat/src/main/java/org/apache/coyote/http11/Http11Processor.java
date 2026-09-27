package org.apache.coyote.http11;

import java.io.IOException;
import java.net.Socket;
import org.apache.coyote.Adapter;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.data.HttpRequest;
import org.apache.coyote.http11.data.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Adapter adapter;

    public Http11Processor(
            final Socket connection,
            final Adapter adapter
    ) {
        this.connection = connection;
        this.adapter = adapter;
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

            final HttpRequest request;
            final HttpResponse response = HttpResponse.create();
            try {
                request = HttpRequest.from(inputStream);
            } catch (InvalidHttpRequestException e) {
                response.badRequest();
                response.setHeader("Connection", "close");
                outputStream.write(response.toString().getBytes());
                outputStream.flush();
                return;
            }

            adapter.service(request, response);

            outputStream.write(response.toString().getBytes());
            outputStream.flush();
        } catch (IOException e) {
            log.error(e.getMessage(), e);
        }
    }
}
