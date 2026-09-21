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
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final Map<String, String> CONTENT_TYPES = Map.of(
            ".html", DEFAULT_CONTENT_TYPE,
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
             final var outputStream = connection.getOutputStream();
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            String method = extractMethod(requestLine);
            Map<String, String> headers = readHeaders(reader);

            String body = "";
            if (method.equals("POST")) {
                body = readBody(reader, headers);
            }
            String requestUri = extractRequestUri(requestLine);
            String path = extractPath(requestUri);

            Map<String, String> params = extractParams(method, requestUri, body);

            String redirectPath = handleRegister(method, path, params);
            if (redirectPath == null) {
                redirectPath = handleLogin(method, path, params);
            }

            if (redirectPath != null) {
                String response = createRedirectResponse(redirectPath);
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            String resourcePath = resolveResourcePath(path);
            InputStream resourceStream = getResourceStream(resourcePath);

            String status = "200 OK";
            if (resourceStream == null) {
                status = "404 Not Found";
                resourcePath = "/404.html";
                resourceStream = getResourceStream(resourcePath);
            }
            String responseBody = getResponseBody(path, resourceStream);
            String response = createResponse(status, resourcePath, responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractMethod(final String requestLine) {
        String[] request = requestLine.split(" ");
        return request[0];
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line = reader.readLine();
        while (line != null && !line.isEmpty()) {
            String[] header = line.split(": ", 2);
            headers.put(header[0], header[1]);
            line = reader.readLine();
        }
        return headers;
    }

    private String readBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return "";
        }
        int length = Integer.parseInt(contentLength);
        char[] body = new char[length];
        reader.read(body, 0, length);
        return new String(body);
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

    private Map<String, String> extractParams(final String method, final String requestUri, final String body) {
        if (method.equals("POST")) {
            return parseQueryString(body);
        }
        return extractQueryParams(requestUri);
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

    private String handleRegister(final String method, final String path, final Map<String, String> params) {
        if (!method.equals("POST") || !path.equals("/register")) {
            return null;
        }

        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("register user: {}", user);

        return "/index.html";
    }

    private String handleLogin(final String method, final String path, final Map<String, String> params) {
        if (!method.equals("POST") || !path.equals("/login")) {
            return null;
        }

        String account = params.get("account");
        String password = params.get("password");
        if (account == null || password == null) {
            return "/401.html";
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            log.info("account doesn't exist: {}", account);
            return "/401.html";
        }

        User foundUser = user.get();
        if (foundUser.checkPassword(password)) {
            log.info("login user: {}", foundUser);
            return "/index.html";
        }

        return "/401.html";
    }

    private String createRedirectResponse(final String location) {
        return String.format(
                "HTTP/1.1 302 Found\r\n"
                        + "Location: %s\r\n"
                        + "\r\n",
                location
        );
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
        try (InputStream stream = resourceStream) {
            if (path.equals("/")) {
                return "Hello world!";
            }
            return new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String createResponse(final String status, final String resourcePath, final String responseBody) {
        final String contentType = getContentType(resourcePath);
        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);
        return String.format(
                "HTTP/1.1 %s\r\n"
                        + "Content-Type: %s\r\n"
                        + "Content-Length: %d\r\n"
                        + "\r\n"
                        + "%s",
                status,
                contentType,
                responseBodyBytes.length,
                responseBody
        );
    }

    private String getContentType(final String resourcePath) {
        final int lastSlashIndex = resourcePath.lastIndexOf("/");
        final int lastDotIndex = resourcePath.lastIndexOf(".");

        if (lastDotIndex <= lastSlashIndex) {
            return DEFAULT_CONTENT_TYPE;
        }
        final String extension = resourcePath.substring(lastDotIndex).toLowerCase(Locale.ROOT);
        return CONTENT_TYPES.getOrDefault(extension, DEFAULT_CONTENT_TYPE);
    }
}
