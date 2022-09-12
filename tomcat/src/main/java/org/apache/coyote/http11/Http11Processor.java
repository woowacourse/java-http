package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import nextstep.jwp.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.ResponseEntity;
import org.apache.coyote.http11.response.file.FileHandler;
import org.apache.coyote.http11.servlet.HttpFrontServlet;
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
        try (final var bufferedReader = new BufferedReader(
                new InputStreamReader(connection.getInputStream(), StandardCharsets.UTF_8));
             final var outputStream = connection.getOutputStream()) {
            final HttpRequest httpRequest = InputStreamHandler.createRequest(bufferedReader);
            final ResponseEntity response = handleRequest(httpRequest);
            final HttpResponse httpResponse = HttpResponse.of(response);

            writeResponse(outputStream, httpResponse.createResponse());
        } catch (final IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseEntity handleRequest(final HttpRequest httpRequest)
            throws IOException {
        final String path = httpRequest.getPath();

        if (FileHandler.isStaticFilePath(path)) {
            return FileHandler.createFileResponse(path);
        }

        final HttpFrontServlet frontServlet = HttpFrontServlet.getInstance();
        return frontServlet.service(httpRequest);
    }

    private void writeResponse(final OutputStream outputStream, final String response) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }
}
