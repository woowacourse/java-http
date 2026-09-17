package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_ROOT = "static";

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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final var requestLine = readRequestLine(bufferedReader);
            if (requestLine == null) {
                return;
            }
            final var requestHeaders = readHeaders(bufferedReader);
            final var path = parsePath(requestLine);

            var statusLine = "HTTP/1.1 200 OK ";
            final byte[] responseBody;
            if (path.equals("/")) {
                responseBody = "Hello world!".getBytes();
            } else {
                var resourceUrl = findResource(path);
                if (resourceUrl == null) {
                    statusLine = "HTTP/1.1 404 Not Found ";
                    resourceUrl = findResource("/404.html");
                }
                log.info("{} -> {}", statusLine, resourceUrl);
                responseBody = Files.readAllBytes(Path.of(resourceUrl.toURI()));
            }

            final var response = buildResponse(statusLine, "text/html;charset=utf-8", responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String readRequestLine(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        log.info("{}", requestLine);
        return requestLine;
    }

    private String readHeaders(final BufferedReader reader) throws IOException {
        final var headers = new StringBuilder();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            headers.append(headerLine).append("\r\n");
        }
        return headers.toString();
    }

    private String parsePath(final String requestLine) {
        final String[] tokens = requestLine.split(" ");
        return tokens[1];
    }

    private URL findResource(final String path) {
        return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path);
    }

    private String buildResponse(final String statusLine, final String contentType, final byte[] body) {
        return String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType + " ",
                "Content-Length: " + body.length + " ",
                "",
                new String(body, StandardCharsets.UTF_8));
    }
}
