package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
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

            String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            URI uri = parseRequestLine(requestLine);

            readHeaders(reader);

            handleRequest(uri, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private URI parseRequestLine(String requestLine) {
        String requestUri = requestLine.trim().split("\\s+")[1];
        return URI.create(requestUri);
    }

    private void readHeaders(BufferedReader reader) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty()) {
                return;
            }
        }
    }

    private void handleRequest(URI uri, OutputStream outputStream) throws IOException {
        String path = uri.getPath();

        if ("/".equals(path)) {
            writeResponse(outputStream, "static/index.html");
            return;
        }

        if ("/login".equals(path)) {
            if (uri.getRawQuery() == null) {
                writeResponse(outputStream, "static/login.html");
                return;
            }

            if (handleLogin(uri)) {
                writeRedirectResponse(outputStream, "/index.html");
                return;
            }

            writeRedirectResponse(outputStream, "/401.html");
            return;
        }

        writeResponse(outputStream, "static" + path);
    }

    private boolean handleLogin(URI uri) {
        String query = uri.getRawQuery();
        Map<String, String> params = parseQuery(query);

        String account = params.get("account");
        String password = params.get("password");

        if (account == null || password == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private Map<String, String> parseQuery(String query) {
        if (query == null || query.isBlank()) {
            return Map.of();
        }
        return Arrays.stream(query.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> parts[0],
                        parts -> parts[1]
                ));
    }

    private void writeResponse(OutputStream outputStream, String resourcePath) throws IOException {
        String contentType = URLConnection.guessContentTypeFromName(resourcePath);

        try (InputStream resource = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (resource == null) {
                log.warn("리소스를 찾을 수 없습니다. : {}", resourcePath);
                return;
            }

            byte[] responseBody = resource.readAllBytes();

            String responseHeaders = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    ""
            );

            outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        }
    }

    private void writeRedirectResponse(OutputStream outputStream, String location) throws IOException {
        String responseHeaders = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                ""
        );

        outputStream.write(responseHeaders.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
