package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
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

            final String request = buildRequest(inputStream);
            final String[] requestLines = request.split("\r\n", -1);
            if (requestLines.length == 0 || requestLines[0].isEmpty()) {
                log.error("request is empty");
                return;
            }

            final String[] requestLineContents = requestLines[0].split(" ");
            if (requestLineContents.length < 3) {
                log.error("request line is not enough");
                return;
            }

            final String method = requestLineContents[0];
            final String uri = requestLineContents[1];
            final String version = requestLineContents[2];

            String path = uri;
            Map<String, String> queryParams = new LinkedHashMap<>();
            if (uri.contains("?")) {
                int index = uri.indexOf("?");
                path = uri.substring(0, index);
                String queryString = uri.substring(index + 1);
                queryParams = parseQueryString(queryString);
            }

            final Map<String, String> responseHeaders = new LinkedHashMap<>();
            final String statusLine = "HTTP/1.1 200 OK";
            String responseBody = "Hello world!";
            responseHeaders.put("Content-Type", "text/html;charset=utf-8");

            if ("GET".equals(method)) {
                if ("/login".equals(path)) {
                    responseBody = handleLogin(queryParams);
                    responseHeaders.put("Content-Type", "text/html");
                } else if (!"/".equals(path)) {
                    try {
                        final String resourcePath = "static" + path;
                        final URL resource = getClass().getClassLoader().getResource(resourcePath);
                        if (resource != null) {
                            final File file = new File(resource.getFile());
                            if (file.exists()) {
                                responseBody = new String(Files.readAllBytes(file.toPath()));
                                if (path.endsWith(".css")) { // TODO: 다양한 content type 지원
                                    responseHeaders.put("Content-Type", "text/css");
                                }
                            }
                        }
                    } catch (IOException e) {
                        log.error("Failed to read file: {}", path, e);
                    }
                }
            }

            responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes().length));

            final String response = buildResponse(statusLine, responseHeaders, responseBody);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String buildRequest(InputStream inputStream) {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final StringBuilder requestBuilder = new StringBuilder();
        try {
            String line;
            while ((line = reader.readLine()) != null) {
                requestBuilder.append(line).append("\r\n");
                if (line.isEmpty()) {
                    break;
                }
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return requestBuilder.toString();
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

    private String handleLogin(Map<String, String> queryParams) {
        try {
            final String account = queryParams.get("account");
            final String password = queryParams.get("password");
            if (account == null || password == null) {
                log.error("account or password is empty");
                return "account or password is empty";
            }

            final User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow(() -> new IllegalArgumentException("[ERROR] 회원을 찾을 수 없습니다."));

            if (user.checkPassword(password)) {
                log.info("Login successful: {}", user);
                return "Login successful";
            } else {
                return "Invalid password";
            }
        } catch (Exception e) {
            log.error("Login failed", e);
            return "Login failed";
        }
    }

    private String buildResponse(String statusLine, Map<String, String> responseHeaders, String responseBody) {
        final StringBuilder responseBuilder = new StringBuilder();
        responseBuilder.append(statusLine).append("\r\n");
        appendResponseHeaders(responseHeaders, responseBuilder);
        responseBuilder.append("\r\n");
        responseBuilder.append(responseBody);
        return responseBuilder.toString();
    }

    private void appendResponseHeaders(Map<String, String> responseHeaders, StringBuilder responseBuilder) {
        for (Entry<String, String> entry : responseHeaders.entrySet()) {
            responseBuilder.append(entry.getKey())
                    .append(": ")
                    .append(entry.getValue())
                    .append("\r\n");
        }
    }
}
