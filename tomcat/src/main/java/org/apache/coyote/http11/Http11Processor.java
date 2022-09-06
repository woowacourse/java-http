package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Objects;
import java.util.UUID;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String HTTP_VERSION = "HTTP/1.1 ";

    private final Logger log;
    private final Socket connection;
    private final UserService userService;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this.log = LoggerFactory.getLogger(getClass());
        this.connection = connection;
        this.userService = new UserService();
        this.sessionManager = new SessionManager();
    }

    @Override
    public void run() {
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest httpRequest = HttpRequest.from(bufferedReader);
            final String response = route(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String route(final HttpRequest httpRequest) {
        final String requestUri = httpRequest.getUri();
        log.info("request method: {}, uri: {}", httpRequest.getMethod(), requestUri);

        if (requestUri.equals("/login")) {
            return createLoginResponse(httpRequest);
        }
        if (requestUri.equals("/register")) {
            return createRegisterResponse(httpRequest);
        }
        if (!requestUri.equals("/")) {
            return createResponse(StatusCode.OK, ContentType.findByUri(requestUri), createResponseBody(requestUri));
        }

        return createResponse(StatusCode.OK, ContentType.HTML, "Hello world!");
    }

    private String createLoginResponse(final HttpRequest httpRequest) {
        if (httpRequest.isPostMethod()) {
            return login(httpRequest);
        }
        return createResponse(StatusCode.OK, ContentType.HTML, createResponseBody("/login.html"));
    }

    private String login(final HttpRequest httpRequest) {
        final StatusCode statusCode = StatusCode.FOUND;
        try {
            final User user = userService.login(httpRequest);
            final String sessionId = UUID.randomUUID().toString();
            final Session session = new Session(sessionId);
            sessionManager.add(session);
            session.setAttribute("user", user);

            return createLoginSuccessResponse(statusCode, sessionId);
        } catch (final IllegalArgumentException e) {
            return createRedirectResponse(statusCode, "/401.html");
        }
    }

    private String createRegisterResponse(final HttpRequest httpRequest) {
        if (httpRequest.isPostMethod()) {
            userService.register(httpRequest);
            return createRedirectResponse(StatusCode.FOUND, "/index.html");
        }

        return createResponse(StatusCode.OK, ContentType.HTML, createResponseBody("/register.html"));
    }

    private String createResponseBody(final String pathUri) {
        final URL url = getClass().getClassLoader().getResource("static" + pathUri);
        Objects.requireNonNull(url);

        try {
            final File file = new File(url.getPath());
            final Path path = file.toPath();
            return new String(Files.readAllBytes(path));
        } catch (final IOException e) {
            log.error("invalid resource", e);
            throw new IllegalArgumentException("파일을 찾을 수 없습니다.");
        }
    }

    private String createResponse(final StatusCode statusCode, final ContentType contentType,
                                  final String responseBody) {
        return String.join("\r\n",
                HTTP_VERSION + statusCode + " ",
                "Content-Type: " + contentType.getValue() + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createRedirectResponse(final StatusCode statusCode, final String location) {
        return String.join("\r\n",
                HTTP_VERSION + statusCode.toString() + " ",
                "Location: " + location + " ",
                "");
    }

    private String createLoginSuccessResponse(final StatusCode statusCode, final String jSessionCookie) {
        return String.join("\r\n",
                HTTP_VERSION + statusCode.toString() + " ",
                "Location: /index.html" + " ",
                "Set-Cookie: JSESSIONID=" + jSessionCookie + " ",
                "");
    }
}
