package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import org.apache.catalina.ServletContainer;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final ServletContainer container;

    public Http11Processor(final Socket connection, final ServletContainer container) {
        this.connection = connection;
        this.container = container;
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
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final HttpRequest request = parseRequest(requestLine);
            final HttpResponse response = new HttpResponse(outputStream);

            container.service(request, response);

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final String requestLine) {
        final String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 2) {
            throw new IllegalArgumentException("Invalid request line: " + requestLine);
        }

        final String method = requestParts[0];
        final String fullUrl = requestParts[1];

        String uri = fullUrl;
        String queryString = "";

        if (fullUrl.contains("?")) {
            final String[] urlParts = fullUrl.split("\\?", 2);
            uri = urlParts[0];
            queryString = urlParts[1];
        }

        return new HttpRequest(method, uri, queryString);
    }
}
