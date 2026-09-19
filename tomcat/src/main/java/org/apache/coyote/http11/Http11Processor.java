package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String requestUri = requestLine.split(" ")[1];
            final String path = resolvePath(requestUri);

            String responseBody = "Hello world!";
            if ("/index.html".equals(path) || "/css/styles.css".equals(path) || "/login.html".equals(path)) {
                final var resource = getClass().getClassLoader().getResource("static" + path);
                if (resource == null) {
                    throw new IOException("Resource not found: " + path);
                }
                responseBody = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
            }

            String contentType = "text/html";
            if (path.endsWith(".css")) {
                contentType = "text/css";
            }
            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolvePath(final String requestUri) {
        final int queryIndex = requestUri.indexOf('?');
        String path = requestUri;
        if (queryIndex >= 0) {
            path = requestUri.substring(0, queryIndex);
        }

        if (!"/login".equals(path)) {
            return path;
        }

        login(requestUri, queryIndex);
        return "/login.html";
    }

    private void login(final String requestUri, final int queryIndex) {
        if (queryIndex < 0) {
            return;
        }

        final Map<String, String> parameters = parseQueryString(requestUri.substring(queryIndex + 1));
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("Login succeeded: account={}", user.getAccount()));
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();
        for (String parameter : queryString.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                parameters.put(
                        URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }
}
