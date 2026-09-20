package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.*;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import javax.annotation.Nonnull;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8));
             final var outputStream = connection.getOutputStream()) {

            String requestUri = readRequestUri(reader);
            String[] pathParts = requestUri.split("\\?", 2);
            String path = pathParts[0];

            Map<String, String> queryParameters = new HashMap<>();
            if (pathParts.length > 1) {
                queryParameters = parseQueryParameters(pathParts[1]);
            }

            if (path.equals("/login")) {
                logLoginResult(queryParameters);
                serveStaticFile(reader, "/login.html", outputStream);
                return;
            }
            if (path.endsWith(".html") || path.endsWith(".css") || path.endsWith(".js")) {
                serveStaticFile(reader, path, outputStream);
                return;
            }
            if (path.equals("/")) {
                serverHomePage(outputStream);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestUri(BufferedReader reader) throws IOException {
        String line = reader.readLine();

        if (line == null) {
            throw new IOException("EOF: No request line received");
        }

        String[] parts = line.split(" ");
        if (parts.length != 3) {
            throw new IOException("Invalid line received: " + line);
        }

        return parts[1];
    }

    private Map<String, String> parseQueryParameters(String queryString) {
        Map<String, String> queryParameters = new HashMap<>();

        for (String param : queryString.split("&")) {
            if (param.isEmpty()) continue;

            String[] keyValue = param.split("=", 2);
            String key = URLDecoder.decode(keyValue[0], UTF_8);
            String value = keyValue.length > 1 ? URLDecoder.decode(keyValue[1], UTF_8) : "";
            queryParameters.put(key, value);
        }

        return queryParameters;
    }

    private void logLoginResult(Map<String, String> queryParameters) {
        String account = queryParameters.get("account");
        String password = queryParameters.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> log.info("user: {}", user),
                        () -> log.info("로그인 실패: account={}", account)
                );
    }

    private void serveStaticFile(BufferedReader reader, String requestUri, OutputStream outputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        readRequestHeader(reader, headers);

        URL resource = Objects.requireNonNull(getClass().getClassLoader().getResource("static" + requestUri));

        Path path = Path.of(resource.getPath());

        byte[] responseBody = Files.readAllBytes(path);
        String contentType = findContentType(requestUri);
        final var response = createHtmlOkResponseHeader(contentType, responseBody.length);

        writeResponse(outputStream, response.getBytes(UTF_8), responseBody);
    }

    private String findContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private void serverHomePage(OutputStream outputStream) throws IOException {
        final var responseBody = "Hello world!";

        final var response = createHtmlOkResponseHeader(findContentType("/"), responseBody.getBytes().length);

        writeResponse(outputStream, response.getBytes(), responseBody.getBytes(UTF_8));
    }

    private void readRequestHeader(BufferedReader reader, Map<String, String> headers) throws IOException {
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(" ");
            headers.put(headerParts[0].trim(), headerParts[1].trim());
        }
    }

    @Nonnull
    private String createHtmlOkResponseHeader(String contentType, int contentLength) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                ""
        );
    }

    private void writeResponse(OutputStream outputStream, byte[] response, byte[] responseBody)
            throws IOException {
        outputStream.write(response);
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
