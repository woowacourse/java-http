package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_PATH = "/index.html";
    private static final String INDEX_RESOURCE = "static/index.html";
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String DEFAULT_BODY = "Hello world!";

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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            readHeaders(reader);

            final var parsedRequestLine = RequestLine.parse(requestLine);
            final var response = createResponse(parsedRequestLine);
            response.writeTo(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readHeaders(final BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                return;
            }
        }
    }

    private HttpResponse createResponse(final RequestLine requestLine) throws IOException {
        if (!requestLine.targets(INDEX_PATH)) {
            return HttpResponse.html(DEFAULT_BODY);
        }

        return HttpResponse.html(readResource(INDEX_RESOURCE));
    }

    private byte[] readResource(final String resourcePath) throws IOException {
        final var classLoader = Http11Processor.class.getClassLoader();

        try (final var resource = classLoader.getResourceAsStream(resourcePath)) {
            if (resource == null) {
                throw new IOException("Resource not found: " + resourcePath);
            }

            return resource.readAllBytes();
        }
    }

    private record RequestLine(String method, String target, String protocol) {

        private static final int REQUEST_PART_COUNT = 3;

        private RequestLine {
            Objects.requireNonNull(method);
            Objects.requireNonNull(target);
            Objects.requireNonNull(protocol);
        }

        private static RequestLine parse(final String value) {
            final var requestParts = value.trim().split("\\s+");

            if (requestParts.length != REQUEST_PART_COUNT) {
                throw new UncheckedServletException(
                        new IllegalArgumentException("Invalid request line: " + value)
                );
            }

            return new RequestLine(requestParts[0], requestParts[1], requestParts[2]);
        }

        private boolean targets(final String path) {
            return target.equals(path);
        }
    }

    private static final class HttpResponse {

        private static final String STATUS_LINE = "HTTP/1.1 200 OK ";

        private final String contentType;
        private final byte[] body;

        private HttpResponse(final String contentType, final byte[] body) {
            this.contentType = Objects.requireNonNull(contentType);
            this.body = Objects.requireNonNull(body).clone();
        }

        private static HttpResponse html(final byte[] body) {
            return new HttpResponse(HTML_CONTENT_TYPE, body);
        }

        private static HttpResponse html(final String body) {
            return html(body.getBytes(StandardCharsets.UTF_8));
        }

        private void writeTo(final OutputStream outputStream) throws IOException {
            final var headers = String.join("\r\n",
                    STATUS_LINE,
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + body.length + " ",
                    "",
                    ""
            );

            outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
            outputStream.write(body);
            outputStream.flush();
        }
    }
}
