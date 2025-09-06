package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
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
        try (
                final var inputStream = connection.getInputStream();
                final var inputReader = new BufferedReader(new InputStreamReader(inputStream));
                final var outputStream = connection.getOutputStream()
        ) {
            final String line = inputReader.readLine();
            if (line == null || line.isBlank()) {
                final String responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            final String requestTarget = line.split(" ")[1];
            String path = requestTarget;
            String queryString = "";
            if (requestTarget.contains("?")) {
                int queryIndex = requestTarget.indexOf("?");
                path = requestTarget.substring(0, queryIndex);
                queryString = requestTarget.substring(queryIndex + 1);
            }
            final var queryParams = parseQueryString(queryString);
            String filePath = path;

            if ("/login".equals(path)) {
                if (queryParams.containsKey("account") && queryParams.containsKey("password")) {
                    final String account = queryParams.get("account");
                    final String password = queryParams.get("password");
                    InMemoryUserRepository.findByAccount(account)
                            .filter(user -> user.checkPassword(password))
                            .ifPresent(user -> log.info("login success: {}", user));
                }
                filePath = "/login.html";
            }
            if ("/".equals(filePath)) {
                final String responseBody = "Hello world!";
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: text/html;charset=utf-8 ",
                        "Content-Length: " + responseBody.getBytes().length + " ",
                        "",
                        responseBody);
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }
            final String resourcePath = "static" + filePath;
            try (
                    final var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)
            ) {
                if (resourceStream == null) {
                    final String responseBody = "File Not Found!";
                    final var response = String.join("\r\n",
                            "HTTP/1.1 404 Not Found ",
                            "Content-Type: text/html;charset=utf-8 ",
                            "Content-Length: " + responseBody.getBytes().length + " ",
                            "",
                            responseBody);
                    outputStream.write(response.getBytes());
                    outputStream.flush();
                    return;
                }
                final byte[] responseBody = resourceStream.readAllBytes();
                final String contentType = getContentType(filePath);
                final var response = String.join("\r\n",
                        "HTTP/1.1 200 OK ",
                        "Content-Type: " + contentType + " ",
                        "Content-Length: " + responseBody.length + " ",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.write(responseBody);
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return Collections.emptyMap();
        }
        final Map<String, String> params = new HashMap<>();
        final String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                params.put(key, value);
            }
        }
        return params;
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
