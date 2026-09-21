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
import java.util.Optional;

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

            final String[] requestParts = requestLine.split(" ", 3);
            final String method = requestParts[0];
            final String requestUri = requestParts[1];
            final Optional<String> redirectLocation = resolveRedirect(method, requestUri);
            if (redirectLocation.isPresent()) {
                final String response = redirectResponse(redirectLocation.get());
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final String path = resolvePath(requestUri);

            String responseBody = "Hello world!";
            if (!"/".equals(path)) {
                final var resource = getClass().getClassLoader().getResource("static" + path);
                if (resource != null) {
                    responseBody = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
                }
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

    private Optional<String> resolveRedirect(final String method, final String requestUri) {
        final int queryIndex = requestUri.indexOf('?');
        final String path = queryIndex >= 0 ? requestUri.substring(0, queryIndex) : requestUri;
        if (!"GET".equals(method) || !"/login".equals(path) || queryIndex < 0) {
            return Optional.empty();
        }

        return Optional.of(login(requestUri.substring(queryIndex + 1))
                ? "/index.html"
                : "/401.html");
    }

    private String redirectResponse(final String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location + " ",
                "Content-Length: 0 ",
                "",
                "");
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

        return "/login.html";
    }

    private boolean login(final String queryString) {
        final Map<String, String> parameters = parseQueryString(queryString);
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        if (account == null || password == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    log.info("Login succeeded: account={}", user.getAccount());
                    return true;
                })
                .orElse(false);
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
