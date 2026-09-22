package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        requestMapping = new RequestMapping();
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
            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final var request = requestWithBody(reader, HttpRequest.from(reader));
            final var response = requestMapping.getController(request).service(request);

            writeResponse(outputStream, response);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest requestWithBody(final BufferedReader reader, final HttpRequest request) throws IOException {
        if (!request.method().equals("POST")) {
            return request;
        }

        final var contentLength = Integer.parseInt(request.header("Content-Length"));
        final var buffer = new char[contentLength];
        var totalRead = 0;

        while (totalRead < contentLength) {
            final var read = reader.read(buffer, totalRead, contentLength - totalRead);

            if (read == -1) {
                throw new IOException("Request body ended before Content-Length");
            }

            totalRead += read;
        }

        return request.withBody(new String(buffer));
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.headerBytes());
        outputStream.write(response.body());
        outputStream.flush();
    }
}
