package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String CRLF = "\r\n";

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

            final Http11Request request = new Http11Request(inputStream);

            final String method = request.getMethod();
            final String uri = request.getUri();
            final String version = request.getVersion();

            String path = uri;
            Map<String, String> queryParams = new LinkedHashMap<>();
            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);
                String queryString = uri.substring(index + 1);
                queryParams = parseQueryString(queryString);
            }

            final Map<String, String> requestHeaders = request.getHeaders();
            final String requestBody = request.getBody();

            final Map<String, String> responseHeaders = new LinkedHashMap<>();
            String statusLine = "HTTP/1.1 200 OK";
            String responseBody = "Hello world!";
            responseHeaders.put("Content-Type", MediaType.detectMimeType(path));

            if ("GET".equals(method)) {
                if ("/register".equals(path)) {
                    responseBody = readFileFromClasspath("static/register.html");
                } else if ("/login".equals(path)) {
                    if (queryParams.isEmpty()) {
                        responseBody = readFileFromClasspath("static/login.html");
                    }
                } else if (!"/".equals(path)) {
                    final String resourcePath = "static" + path;
                    responseBody = readFileFromClasspath(resourcePath);
                }
            }

            if ("POST".equals(method)) {
                if ("/register".equals(path)) {
                    Map<String, String> params = new HashMap<>();
                    String[] pairs = requestBody.split("&");

                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=", 2);
                        if (keyValue.length == 2) {
                            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                            params.put(key, value);
                        }
                    }

                    User user = new User(params.get("account"), params.get("password"), params.get("email"));
                    log.info("User saved: {}", user);
                    InMemoryUserRepository.save(user);

                    statusLine = "HTTP/1.1 302 Found";
                    if (requestHeaders.containsKey("Cookie")) {
                        Http11Cookie cookie = new Http11Cookie(requestHeaders.get("Cookie"));
                        if (cookie.isNotContainsSessionId()) {
                            responseHeaders.put("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
                        }
                    } else {
                        responseHeaders.put("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
                    }
                    responseHeaders.put("Location", "/index.html");
                }

                if ("/login".equals(path)) {
                    Map<String, String> params = new HashMap<>();
                    String[] pairs = requestBody.split("&");

                    for (String pair : pairs) {
                        String[] keyValue = pair.split("=", 2);
                        if (keyValue.length == 2) {
                            String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                            String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                            params.put(key, value);
                        }
                    }

                    final User user = InMemoryUserRepository.findByAccount(params.get("account"))
                            .orElseThrow(() -> new IllegalArgumentException("[ERROR] 회원을 찾을 수 없습니다."));                    log.info("User saved: {}", user);

                    if (user.checkPassword(params.get("password"))) {
                        statusLine = "HTTP/1.1 302 Found";
                        if (requestHeaders.containsKey("Cookie")) {
                            Http11Cookie cookie = new Http11Cookie(requestHeaders.get("Cookie"));
                            if (cookie.isNotContainsSessionId()) {
                                responseHeaders.put("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
                            }
                        } else {
                            responseHeaders.put("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
                        }
                        responseHeaders.put("Location", "/index.html");
                    } else {
                        statusLine = "HTTP/1.1 302 Found";
                        responseHeaders.put("Location", "/401.html");
                    }
                }
            }

            responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));

            final String response = buildResponse(statusLine, responseHeaders, responseBody);
            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        final Map<String, String> queryParams = new LinkedHashMap<>();
        if (queryString == null || queryString.isEmpty()) {
            log.error("query string is empty");
            return queryParams;
        }

        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            final String[] keyValue = pair.split("=");
            queryParams.put(keyValue[0], keyValue[1]);
        }
        return queryParams;
    }

    private String readFileFromClasspath(String resourcePath) {
        InputStream input = getClass().getClassLoader().getResourceAsStream(resourcePath);
        StringBuilder fileContents = new StringBuilder();
        if (input == null) {
            log.error("resource not found: {}", resourcePath);
            return fileContents.toString();
        }
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(input))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContents.append(line).append(CRLF);
            }
        } catch (IOException e) {
            log.error("Failed to read file: {}", resourcePath, e);
        }
        return fileContents.toString();
    }

    private String buildResponse(String statusLine, Map<String, String> responseHeaders, String responseBody) {
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine).append(CRLF);
        appendResponseHeaders(responseHeaders, responseBuilder);
        responseBuilder.append(CRLF);
        responseBuilder.append(responseBody);
        return responseBuilder.toString();
    }

    private void appendResponseHeaders(Map<String, String> responseHeaders, StringBuilder responseBuilder) {
        for (Entry<String, String> entry : responseHeaders.entrySet()) {
            responseBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append(CRLF);
        }
    }
}
