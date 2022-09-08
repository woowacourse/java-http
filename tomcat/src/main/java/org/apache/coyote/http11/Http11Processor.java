package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import nextstep.jwp.service.UserService;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.Headers;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.StatusCode;
import org.apache.coyote.http11.response.StatusLine;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String DEFAULT_PAGE = "/index.html";

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
            final HttpResponse response = route(httpRequest);
            final String responseAsString = response.toString();

            outputStream.write(responseAsString.getBytes());
            outputStream.flush();
        } catch (final IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse route(final HttpRequest httpRequest) {
        final String requestUri = httpRequest.getUri();
        log.info("request method: {}, uri: {}", httpRequest.getMethod(), requestUri);

        if (requestUri.equals("/login")) {
            return createLoginResponse(httpRequest);
        }
        if (requestUri.equals("/register")) {
            return createRegisterResponse(httpRequest);
        }
        if (!requestUri.equals("/")) {
            return createResponse(StatusCode.OK, ContentType.findByUri(requestUri), createResponseBody(requestUri),
                    httpRequest);
        }

        return createResponse(StatusCode.OK, ContentType.HTML, "Hello world!", httpRequest);
    }

    private HttpResponse createLoginResponse(final HttpRequest httpRequest) {
        final Optional<String> jSessionId = httpRequest.findCookie("JSESSIONID");
        if (jSessionId.isPresent() && sessionManager.findSession(jSessionId.get()).isPresent()) {
            return createRedirectResponse(StatusCode.FOUND, DEFAULT_PAGE, httpRequest);
        }
        if (httpRequest.isPostMethod()) {
            return login(httpRequest);
        }
        return createResponse(StatusCode.OK, ContentType.HTML, createResponseBody("/login.html"), httpRequest);
    }

    private HttpResponse login(final HttpRequest httpRequest) {
        final StatusCode statusCode = StatusCode.FOUND;
        try {
            final User user = userService.login(httpRequest);
            final String sessionId = UUID.randomUUID().toString();
            final Session session = new Session(sessionId);
            sessionManager.add(session);
            session.setAttribute("user", user);

            return createLoginSuccessResponse(statusCode, sessionId, httpRequest);
        } catch (final IllegalArgumentException e) {
            return createRedirectResponse(statusCode, "/401.html", httpRequest);
        }
    }

    private HttpResponse createRegisterResponse(final HttpRequest httpRequest) {
        if (httpRequest.isPostMethod()) {
            userService.register(httpRequest);
            return createRedirectResponse(StatusCode.FOUND, DEFAULT_PAGE, httpRequest);
        }

        return createResponse(StatusCode.OK, ContentType.HTML, createResponseBody("/register.html"), httpRequest);
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

    private HttpResponse createResponse(final StatusCode statusCode, final ContentType contentType,
                                        final String responseBody, final HttpRequest httpRequest) {
        final StatusLine statusLine = StatusLine.from(httpRequest.getProtocolVersion(), statusCode);
        final Headers headers = Headers.from(Map.entry("Content-Type", contentType.getValue() + ";charset=utf-8"),
                Map.entry("Content-Length", String.valueOf(responseBody.getBytes().length)));

        return HttpResponse.from(statusLine, headers, responseBody);
    }

    private HttpResponse createRedirectResponse(final StatusCode statusCode, final String location,
                                                final HttpRequest httpRequest) {
        final StatusLine statusLine = StatusLine.from(httpRequest.getProtocolVersion(), statusCode);
        final Headers headers = Headers.from(Map.entry("Location", location));
        return HttpResponse.from(statusLine, headers, "");
    }

    private HttpResponse createLoginSuccessResponse(final StatusCode statusCode, final String jSessionCookie,
                                                    final HttpRequest httpRequest) {
        final StatusLine statusLine = StatusLine.from(httpRequest.getProtocolVersion(), statusCode);
        final Headers headers = Headers.from(Map.entry("Location", DEFAULT_PAGE),
                Map.entry("Set-Cookie", "JSESSIONID=" + jSessionCookie));
        return HttpResponse.from(statusLine, headers, "");
    }


}
