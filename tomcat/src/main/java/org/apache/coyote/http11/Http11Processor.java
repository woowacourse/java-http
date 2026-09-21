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
    private static final String FOUND_STATUS = "302 Found";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String CONTENT_LENGTH_HEADER = "Content-Length";
    private static final String LOCATION_HEADER = "Location";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
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

            final var response = responseFor(responseContentFor(requestUri.path()), newSessionIdFor(requestCookie));
            writeResponse(outputStream, response);
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
        var totalRead = 0;

        while (totalRead < contentLength) {
            final var read = reader.read(buffer, totalRead, contentLength - totalRead);

            if (read == -1) {
                throw new IOException("Request body ended before Content-Length");
            }

            totalRead += read;
        }

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

    private HttpResponse responseFor(
            final ResponseContent responseContent,
            final Optional<String> newSessionId
    ) {
        final var body = responseContent.body();
        final var response = HttpResponse.of(responseContent.status() + " ", body);

        return withSessionCookie(response, newSessionId)
                .withHeader(CONTENT_TYPE_HEADER, responseContent.contentType() + " ")
                .withHeader(CONTENT_LENGTH_HEADER, body.length + " ");
    }

    private void redirect(
            final OutputStream outputStream,
            final String location,
            final Optional<String> newSessionId
    ) throws IOException {
        writeResponse(outputStream, redirectResponseFor(location, newSessionId));
    }

    private HttpResponse redirectResponseFor(final String location, final Optional<String> newSessionId) {
        final var response = HttpResponse.of(FOUND_STATUS, new byte[0]);

        return withSessionCookie(response, newSessionId)
                .withHeader(LOCATION_HEADER, location)
                .withHeader(CONTENT_LENGTH_HEADER, "0");
    }

    private HttpResponse withSessionCookie(final HttpResponse response, final Optional<String> newSessionId) {
        return newSessionId
                .map(sessionId -> response.withHeader(
                        SET_COOKIE_HEADER,
                        SESSION_ID_COOKIE_NAME + "=" + sessionId
                ))
                .orElse(response);
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(response.headerBytes());
        outputStream.write(response.body());
        outputStream.flush();
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
