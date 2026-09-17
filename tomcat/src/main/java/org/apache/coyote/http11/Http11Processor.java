package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", "text/html;charset=utf-8",
            ".css", "text/css;charset=utf-8",
            ".js", "application/javascript;charset=utf-8"
    );

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

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream)
            );

            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            readHeaders(reader);

            String requestUri = extractRequestUri(requestLine);

            String path = extractPath(requestUri);
            Map<String, String> queryParams = extractQueryParams(requestUri);
            handleLogin(path, queryParams);

            String resourcePath = resolveResourcePath(path);
            InputStream resourceStream = getResourceStream(resourcePath);

            String responseBody = getResponseBody(path, resourceStream);
            String response = createResponse(resourcePath, responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void readHeaders(final BufferedReader reader) throws IOException {
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            line = reader.readLine();
        }
    }

    private String extractRequestUri(final String requestLine) {
        String[] request = requestLine.split(" ");
        return request[1];
    }

    private String extractPath(final String requestUri) {
        int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex == -1) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }

    private Map<String, String> extractQueryParams(final String requestUri) {
        int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex == -1) {
            return Map.of();
        }
        String queryString = requestUri.substring(queryStringIndex + 1);
        return parseQueryString(queryString);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            params.put(keyValue[0], keyValue[1]);
        }
        return params;
    }

    private void handleLogin(final String path, final Map<String, String> queryParams) {
        if (!path.equals("/login")) {
            return;
        }
        String account = queryParams.get("account");
        String password = queryParams.get("password");
        if (account == null || password == null) {
            return;
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return;
        }
        User foundUser = user.get();
        if (foundUser.checkPassword(password)) {
            log.info("login user: {}", foundUser);
        }
    }

    private String resolveResourcePath(final String path) {
        if (path.equals("/")) {
            return path;
        }
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        if (!fileName.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private InputStream getResourceStream(final String resourcePath) {
        String path = "static" + resourcePath;
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
        return classLoader.getResourceAsStream(path);
    }

    private String getResponseBody(final String path, final InputStream resourceStream) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }
        return new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
    }

    private String createResponse(final String resourcePath, final String responseBody) {
        String contentType = CONTENT_TYPES.entrySet()
                .stream()
                .filter(entry -> resourcePath.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse("text/html;charset=utf-8");
        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        return String.format(
                "HTTP/1.1 200 OK \r\n"
                        + "Content-Type: %s \r\n"
                        + "Content-Length: %d \r\n"
                        + "\r\n"
                        + "%s",
                contentType,
                responseBodyBytes.length,
                responseBody
        );
    }
}
