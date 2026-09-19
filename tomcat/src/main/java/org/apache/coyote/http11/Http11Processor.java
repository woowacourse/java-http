package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String HTML_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String OK_STATUS = "200 OK";
    private static final String NOT_FOUND_STATUS = "404 Not Found";
    private static final String NOT_FOUND_BODY = "404 Not Found";
    private static final String SESSION_ID_COOKIE_NAME = "JSESSIONID";
    private static final String USER_SESSION_ATTRIBUTE = "user";

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
            final var requestCookie = HttpCookie.from(requestHeader.header("Cookie"));
            final var currentSession = sessionFor(requestCookie);

            if (requestHeader.method().equals("GET")
                    && requestUri.path().equals("/login")
                    && currentSession.map(this::getUser).isPresent()) {
                redirect(outputStream, "/index.html", Optional.empty());
                return;
            }

            if (requestHeader.method().equals("POST")) {
                final var requestBody = readRequestBody(reader, requestHeader);
                final var parameters = RequestUri.parseParameters(requestBody);

                if (requestUri.path().equals("/register")) {
                    register(parameters);
                    redirect(outputStream, "/index.html", newSessionIdFor(requestCookie));
                    return;
                }

                if (requestUri.path().equals("/login")) {
                    final var user = authenticatedUser(parameters);

                    if (user.isPresent()) {
                        final var sessionId = requestCookie.value(SESSION_ID_COOKIE_NAME)
                                .orElseGet(() -> UUID.randomUUID().toString());
                        final var session = currentSession.orElseGet(
                                () -> SessionManager.create(sessionId)
                        );
                        session.setAttribute(USER_SESSION_ATTRIBUTE, user.get());
                        final var sessionIdToSet = requestCookie.value(SESSION_ID_COOKIE_NAME).isPresent()
                                ? Optional.<String>empty()
                                : Optional.of(session.getId());

                        redirect(outputStream, "/index.html", sessionIdToSet);
                        return;
                    }

                    redirect(outputStream, "/401.html", newSessionIdFor(requestCookie));
                    return;
                }
            }

            final var responseContent = responseContentFor(requestUri.path());
            final var responseBody = responseContent.body();

            final var responseHeader = responseHeaderFor(responseContent, newSessionIdFor(requestCookie));

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestBody(
            final BufferedReader reader,
            final RequestHeader requestHeader
    ) throws IOException {
        final var contentLength = Integer.parseInt(requestHeader.header("Content-Length"));
        final var buffer = new char[contentLength];

        reader.read(buffer, 0, contentLength);

        return new String(buffer);
    }

    private void register(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");

        InMemoryUserRepository.save(new User(account, password, email));
    }

    private Optional<Session> sessionFor(final HttpCookie requestCookie) {
        return requestCookie.value(SESSION_ID_COOKIE_NAME)
                .flatMap(SessionManager::findSession);
    }

    private Optional<String> newSessionIdFor(final HttpCookie requestCookie) {
        if (requestCookie.value(SESSION_ID_COOKIE_NAME).isPresent()) {
            return Optional.empty();
        }

        return Optional.of(UUID.randomUUID().toString());
    }

    private String responseHeaderFor(
            final ResponseContent responseContent,
            final Optional<String> newSessionId
    ) {
        if (newSessionId.isEmpty()) {
            return String.join("\r\n",
                    "HTTP/1.1 " + responseContent.status() + " ",
                    "Content-Type: " + responseContent.contentType() + " ",
                    "Content-Length: " + responseContent.body().length + " ",
                    "",
                    "");
        }

        return String.join("\r\n",
                "HTTP/1.1 " + responseContent.status() + " ",
                "Set-Cookie: " + SESSION_ID_COOKIE_NAME + "=" + newSessionId.get(),
                "Content-Type: " + responseContent.contentType() + " ",
                "Content-Length: " + responseContent.body().length + " ",
                "",
                "");
    }

    private void redirect(
            final OutputStream outputStream,
            final String location,
            final Optional<String> newSessionId
    ) throws IOException {
        final var responseHeader = redirectResponseHeader(location, newSessionId);

        outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String redirectResponseHeader(final String location, final Optional<String> newSessionId) {
        if (newSessionId.isEmpty()) {
            return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                "");
        }

        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Set-Cookie: " + SESSION_ID_COOKIE_NAME + "=" + newSessionId.get(),
                "Location: " + location,
                "Content-Length: 0",
                "",
                "");
    }

    private User getUser(final Session session) {
        return (User) session.getAttribute(USER_SESSION_ATTRIBUTE);
    }

    private Optional<User> authenticatedUser(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }
}
