package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<Integer, String> HTTP_STATUS_CODES = Map.ofEntries(
            Map.entry(200, "200 OK"),
            Map.entry(302, "302 Found"),
            Map.entry(400, "400 Bad Request"),
            Map.entry(401, "401 Unauthorized"),
            Map.entry(404, "404 Not Found")
    );
    private static final Map<String, String> MIME_TYPES = Map.ofEntries(
            Map.entry("html", "text/html;charset=UTF-8"),
            Map.entry("css", "text/css;charset=UTF-8"),
            Map.entry("js", "application/javascript;charset=UTF-8"),
            Map.entry("ico", "image/x-icon")
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
        try (final InputStream inputStream = connection.getInputStream();
             final OutputStream outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final List<String> headers = getHeaders(bufferedReader);

            final String[] request = headers.getFirst().split(" ");
            final String method = request[0];
            final String requestUri = request[1];
            log.debug("request : {} {}", method, requestUri);

            final int contentLength = getContentLengthFromHeaders(headers);
            final String body = readRequestBody(bufferedReader, contentLength);

            final String path = parsePath(requestUri);
            final URL resource = getResourceUrl(path);

            if (resource == null) {
                sendResponse(generateErrorResponse(404), outputStream);
                return;
            }

            final Map<String, String> queryParams = mergeParameters(
                    extractQueryParams(requestUri),
                    parseQueryString(body)
            );

            if ("POST".equals(method) && !queryParams.isEmpty()) {
                if ("/login.html".equals(path)) {
                    handleLogin(queryParams, outputStream);
                    return;
                }

                if ("/register.html".equals(path)) {
                    handleRegister(queryParams, outputStream);
                    return;
                }
            }

            sendResponse(generateResponse(200, resource), outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private List<String> getHeaders(final BufferedReader bufferedReader) {
        return bufferedReader.lines()
                .takeWhile(line -> !line.isBlank())
                .toList();
    }

    private int getContentLengthFromHeaders(final List<String> headers) {
        return headers.stream()
                .filter(h -> h.startsWith("Content-Length"))
                .map(h -> h.split(":")[1].trim())
                .mapToInt(Integer::parseInt)
                .findFirst()
                .orElse(0);
    }

    private String readRequestBody(final BufferedReader bufferedReader, final int contentLength) throws IOException {
        if (contentLength <= 0) {
            return "";
        }
        char[] bodyChars = new char[contentLength];
        bufferedReader.read(bodyChars);
        return new String(bodyChars);
    }

    private String parsePath(final String requestUri) {
        String path = requestUri;
        if ("/".equals(requestUri)) {
            return path + "index.html";
        }
        if (path.contains("?")) {
            path = path.split("\\?")[0];
        }
        if (!requestUri.contains(".")) {
            path = path + ".html";
        }
        return path;
    }

    private URL getResourceUrl(String path) throws FileNotFoundException {
        return getClass()
                .getClassLoader()
                .getResource("static" + path);
    }

    private void sendResponse(final String response, final OutputStream outputStream) throws IOException {
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private Map<String, String> mergeParameters(
            Map<String, String> queryParams,
            Map<String, String> bodyParams
    ) {
        Map<String, String> result = new HashMap<>(queryParams);
        result.putAll(bodyParams);
        return result;
    }

    private Map<String, String> extractQueryParams(final String uri) {
        if (!uri.contains("?")) {
            return Map.of();
        }
        final String[] split = uri.split("\\?");
        final String queryString = split.length > 1 ? split[1] : "";
        return parseQueryString(queryString);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> queryMap = new HashMap<>();
        if (queryString == null || queryString.isBlank()) {
            return queryMap;
        }

        final String[] pairs = queryString.split("&");
        for (final String pair : pairs) {
            final String[] keyValue = pair.split("=");
            final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            String value = "";
            if (keyValue.length > 1) {
                value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
            }
            queryMap.put(key, value);
        }
        return queryMap;
    }

    private String generateResponse(final int httpStatusCode, final URL resource) throws IOException {
        final String resourceName = resource.getFile();
        final String extension = extractExtension(resourceName);
        final String responseBody = Files.readString(new File(resourceName).toPath());
        final String contentType = MIME_TYPES.getOrDefault(extension, "text/plain");

        return parseResponse(httpStatusCode, contentType, responseBody);
    }

    private String generateRedirectResponse(final int httpStatusCode, final String location) {
        return parseResponse(httpStatusCode, location);
    }

    private String generateErrorResponse(final int httpStatusCode) {
        try {
            if (!HTTP_STATUS_CODES.containsKey(httpStatusCode)) {
                throw new IllegalArgumentException("Unknown HTTP status code: " + httpStatusCode);
            }
            final String extension = "html";
            final URL resource = getResourceUrl("/" + httpStatusCode + "." + extension);
            final String responseBody = Files.readString(new File(resource.getFile()).toPath());
            final String contentType = MIME_TYPES.getOrDefault(extension, "text/plain");

            return parseResponse(httpStatusCode, contentType, responseBody);
        } catch (IOException | NullPointerException e) {
            final String responseBody = String.format("""
                        <html>
                            <head><title>Error</title></head>
                            <body><h1>%s</h1></body>
                        </html>
                    """, HTTP_STATUS_CODES.get(httpStatusCode));

            return parseResponse(httpStatusCode, "text/html", responseBody);
        }
    }

    private String parseResponse(final int httpStatusCode, final String contentType, final String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 " + HTTP_STATUS_CODES.get(httpStatusCode) + " ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String parseResponse(final int httpStatusCode, final String location) {
        return String.join("\r\n",
                "HTTP/1.1 " + HTTP_STATUS_CODES.get(httpStatusCode) + " ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "");
    }

    private String extractExtension(final String resourceName) {
        int dotIndex = resourceName.lastIndexOf(".");
        if (dotIndex == -1) {
            return "";
        }
        return resourceName.substring(dotIndex + 1);
    }

    private void handleLogin(final Map<String, String> queryMap, final OutputStream outputStream) throws IOException {
        final String account = queryMap.get("account");
        final String password = queryMap.get("password");

        if (account == null || password == null || account.isBlank() || password.isBlank()) {
            sendResponse(generateErrorResponse(400), outputStream);
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            sendResponse(generateRedirectResponse(302, "/index.html"), outputStream);
            return;
        }

        sendResponse(generateRedirectResponse(302, "/401.html"), outputStream);
    }

    private void handleRegister(final Map<String, String> queryMap, final OutputStream outputStream) throws IOException {
        final String account = queryMap.get("account");
        final String email = queryMap.get("email");
        final String password = queryMap.get("password");

        if (account == null || email == null || password == null
                || account.isBlank() || password.isBlank() || email.isBlank()) {
            sendResponse(generateErrorResponse(400), outputStream);
            return;
        }

        final Optional<User> existingUser = InMemoryUserRepository.findByAccount(account);
        if (existingUser.isPresent()) {
            sendResponse(generateRedirectResponse(302, "/400.html"), outputStream);
            return;
        }

        final User newUser = new User(account, password, email);
        InMemoryUserRepository.save(newUser);
        log.info("new user : {}", newUser);
        sendResponse(generateRedirectResponse(302, "/index.html"), outputStream);
    }
}
