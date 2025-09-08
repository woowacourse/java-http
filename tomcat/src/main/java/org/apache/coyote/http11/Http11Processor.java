package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
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

            // 헤더 읽기
            final Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                final String[] headerParts = headerLine.split(":", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0].trim().toLowerCase(), headerParts[1].trim());
                }
            }

            final HttpRequest request = parseRequest(requestLine, headers, reader);
            final HttpResponse response = new HttpResponse(outputStream);

            container.service(request, response);

        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpRequest parseRequest(final String requestLine, final Map<String, String> headers, final BufferedReader reader) throws IOException {
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

        // POST 요청이면 본문 읽기
        String body = null;
        if ("POST".equals(method)) {
            final String contentLength = headers.get("content-length");
            if (contentLength != null) {
                try {
                    final int length = Integer.parseInt(contentLength);
                    final char[] buffer = new char[length];
                    reader.read(buffer, 0, length);
                    body = new String(buffer);
                } catch (final NumberFormatException e) {
                    log.warn("Invalid content-length: {}", contentLength);
                }
            }
        }

        return new HttpRequest(method, uri, queryString, body);
    }
}
