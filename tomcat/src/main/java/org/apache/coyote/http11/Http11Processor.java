package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.awt.image.PackedColorModel;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String OK_STATUS = "200 OK";
    private static final String NOT_FOUND_STATUS = "404 Not Found";
    private static final String NOT_FOUND_BODY = "404 Not Found";

    private record ResponseContent(String status, byte[] body, String contentType) {
        private ResponseContent {
            body = body.clone();
        }

        @Override
        public byte[] body() {
            return body.clone();
        }
    }

    private ResponseContent responseContentFor(final String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return textResponseContent(OK_STATUS, "Hello world!");
        }

        final var staticPath = staticPathFor(requestPath);
        final var resourcePath = "static" + staticPath;

        try (final var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return textResponseContent(NOT_FOUND_STATUS, NOT_FOUND_BODY);
            }

            return new ResponseContent(OK_STATUS, resourceStream.readAllBytes(), contentTypeFor(staticPath));
        }
    }

    private ResponseContent textResponseContent(final String status, final String body) {
        return new ResponseContent(status, body.getBytes(StandardCharsets.UTF_8), HTML_CONTENT_TYPE);
    }

    private String staticPathFor(final String requestPath) {
        final var lastSlashIndex = requestPath.lastIndexOf('/');
        final var lastDotIndex = requestPath.lastIndexOf('.');

        if (lastDotIndex > lastSlashIndex) {
            return requestPath;
        }

        return requestPath + ".html";
    }

    private String contentTypeFor(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return CSS_CONTENT_TYPE;
        }

        return HTML_CONTENT_TYPE;
    }

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

            final var reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );
            final var requestHeader = RequestHeader.from(reader);
            final var requestUri = RequestUri.from(requestHeader.path());

            if (isLoginAttempt(requestUri)) {
                final var responseHeader = loginRedirectResponseHeader(requestUri);

                outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final var responseContent = responseContentFor(requestUri.path());
            final var responseBody = responseContent.body();

            final var responseHeader = String.join("\r\n",
                    "HTTP/1.1 " + responseContent.status() + " ",
                    "Content-Type: " + responseContent.contentType() + " ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String loginRedirectResponseHeader(final RequestUri requestUri) {
        final var location = isLoginSuccess(requestUri) ? "/index.html" : "/401.html";

        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                "");
    }

    private boolean isLoginAttempt(final RequestUri requestUri) {
        return requestUri.path().equals("/login") && !requestUri.queryParameters().isEmpty();
    }

    private boolean isLoginSuccess(final RequestUri requestUri) {
        final var account = requestUri.queryParameter("account");
        final var password = requestUri.queryParameter("password");

        if (account == null || password == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private void logLoginResult(final RequestUri requestUri) {
        if (!requestUri.path().equals("/login")) {
            return;
        }

        final var account = requestUri.queryParameter("account");
        final var password = requestUri.queryParameter("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresentOrElse(
                        user -> log.info("login succeeded: user={}", user),
                        () -> log.warn("login failed: account={}", account)
                );
    }
}
