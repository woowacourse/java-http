package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
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

    private record ResponseContent(String body, String contentType) {
    }

    private ResponseContent responseContentFor(final String requestPath) throws IOException {
        if (requestPath.equals("/")) {
            return new ResponseContent("Hello world!", HTML_CONTENT_TYPE);
        }

        final var staticPath = staticPathFor(requestPath);
        final var resourcePath = "static" + staticPath;

        try (final var resourceStream = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resourceStream == null) {
                return new ResponseContent("Hello world!", HTML_CONTENT_TYPE);
            }

            final var body = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
            return new ResponseContent(body, contentTypeFor(staticPath));
        }
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

            logLoginResult(requestUri);

            final var responseContent = responseContentFor(requestUri.path());

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + responseContent.contentType() + " ",
                    "Content-Length: " +  responseContent.body().getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseContent.body());

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
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
