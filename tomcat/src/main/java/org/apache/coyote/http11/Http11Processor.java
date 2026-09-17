package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.HttpRequest;
import org.apache.coyote.HttpResponse;
import org.apache.coyote.Processor;
import org.apache.coyote.Dispatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private final Socket connection;
    private final Dispatcher dispatcher;

    public Http11Processor(final Socket connection, final Dispatcher dispatcher) {
        this.connection = connection;
        this.dispatcher = dispatcher;
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

            Optional<HttpResponse> response = handleRequest(reader);
            if (response.isEmpty()) {
                return;
            }

            writeResponse(outputStream, response.get());
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Optional<HttpResponse> handleRequest(BufferedReader reader) throws IOException {
        try {
            Optional<HttpRequest> request = parseRequest(reader);
            if (request.isEmpty()) {
                return Optional.empty();
            }

            return Optional.of(dispatcher.dispatch(request.get()));
        } catch (BadRequestException e) {
            return Optional.of(HttpResponse.badRequest(
                    "400 Bad Request".getBytes(StandardCharsets.UTF_8)
            ));
        }
    }

    private Optional<HttpRequest> parseRequest(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            return Optional.empty();
        }

        String[] parts = requestLine.split(" ",3);

        if (parts.length != 3) {
            throw new BadRequestException("Invalid request line: " + requestLine);
        }

        URI uri = createUri(parts[1], requestLine);

        return Optional.of(new HttpRequest(
                parts[0],
                uri.getPath(),
                parseQueryParameters(uri.getRawQuery())
        ));
    }

    private URI createUri(String target, String requestLine) {
        try {
            return URI.create(target);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Invalid request line: " + requestLine);
        }
    }

    private Map<String, String> parseQueryParameters(String rawQuery) {
        Map<String, String> query = new HashMap<>();

        if (rawQuery == null || rawQuery.isBlank()) {
            return query;
        }

        for (String parameter : rawQuery.split("&")) {
            addQueryParameter(query, parameter);
        }

        return query;
    }

    private void addQueryParameter(Map<String, String> query, String parameter) {
        String[] keyValue = parameter.split("=");
        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
        String value = "";

        if (keyValue.length == 2) {
            value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
        }

        query.put(key, value);
    }

    private void writeResponse(OutputStream outputStream, HttpResponse response) throws IOException {
        StringBuilder headers = new StringBuilder(String.join("\r\n",
                "HTTP/1.1 " + response.statusCode(),
                "Content-Type: " + response.contentType(),
                "Content-Length: " + response.contentLength(),
                ""
        ));
        response.headers().forEach((name, value) ->
                headers.append(name).append(": ").append(value).append("\r\n"));
        headers.append("\r\n");

        outputStream.write(headers.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(response.body());
        outputStream.flush();
    }
}
