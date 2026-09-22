package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String GET_METHOD = "GET";
    private static final String POST_METHOD = "POST";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String INDEX_PATH = "/index.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final String SESSION_USER_ATTRIBUTE = "user";
    private static final String CRLF = "\r\n";

    private final Socket connection;
    private final ResponseContentResolver responseContentResolver;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection) {
        this(connection, new ResponseContentResolver(), SessionManager.getInstance());
    }

    Http11Processor(final Socket connection, final ResponseContentResolver responseContentResolver) {
        this(connection, responseContentResolver, SessionManager.getInstance());
    }

    Http11Processor(
            final Socket connection,
            final ResponseContentResolver responseContentResolver,
            final SessionManager sessionManager
    ) {
        this.connection = connection;
        this.responseContentResolver = Objects.requireNonNull(responseContentResolver);
        this.sessionManager = Objects.requireNonNull(sessionManager);
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
            final Optional<HttpRequest> parsedRequest;
            try {
                parsedRequest = HttpRequest.readFrom(reader);
            } catch (IOException e) {
                log.warn("Failed to read HTTP request", e);
                return;
            }
            if (parsedRequest.isEmpty()) {
                return;
            }

            final var request = parsedRequest.get();
            final var path = request.uri().getPath();
            if (path == null) {
                return;
            }
            final var session = findSession(request);
            final var response = resolveResponse(request, path, session);

            try {
                writeResponse(outputStream, response);
            } catch (IOException e) {
                log.warn("Failed to write HTTP response", e);
            }
        } catch (IOException | UncheckedServletException e) {
            log.error("Failed to handle HTTP connection", e);
        }
    }

    private Optional<User> findLoginUser(final String requestBody) {
        return UrlEncodedParameters.parse(requestBody)
                .flatMap(this::findLoginUser);
    }

    private Optional<User> findLoginUser(final UrlEncodedParameters parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        if (account.isEmpty() || password.isEmpty()) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account.get())
                .filter(user -> user.checkPassword(password.get()));
    }

    private HttpResponse resolveResponse(
            final HttpRequest request,
            final String path,
            final Optional<Session> session
    ) {
        if (LOGIN_PATH.equals(path) && GET_METHOD.equals(request.method()) && isLoggedIn(session)) {
            return HttpResponse.redirect(INDEX_PATH);
        }
        if (LOGIN_PATH.equals(path) && POST_METHOD.equals(request.method())) {
            return resolveLoginResponse(request.body(), session);
        }
        if (REGISTER_PATH.equals(path) && POST_METHOD.equals(request.method())) {
            return resolveRegisterResponse(request.body());
        }
        return resolveStaticResponse(path);
    }

    private Optional<Session> findSession(final HttpRequest request) {
        final var cookies = request.headers()
                .firstValue(COOKIE_HEADER)
                .map(HttpCookies::parse)
                .orElseGet(HttpCookies::empty);
        return cookies.get(SESSION_COOKIE_NAME)
                .map(sessionManager::findSession);
    }

    private HttpResponse addSessionCookie(final HttpResponse response, final Session session) {
        return response.addHeader(SET_COOKIE_HEADER, SESSION_COOKIE_NAME + "=" + session.getId());
    }

    private boolean isLoggedIn(final Optional<Session> session) {
        return session
                .map(value -> value.getAttribute(SESSION_USER_ATTRIBUTE) instanceof User)
                .orElse(false);
    }

    private HttpResponse resolveRegisterResponse(final String requestBody) {
        final var parameters = UrlEncodedParameters.parse(requestBody);
        if (parameters.isEmpty()) {
            return HttpResponse.error(HttpStatus.BAD_REQUEST);
        }

        return register(parameters.get());
    }

    private HttpResponse register(final UrlEncodedParameters parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");
        if (account.isEmpty() || password.isEmpty() || email.isEmpty()) {
            return HttpResponse.error(HttpStatus.BAD_REQUEST);
        }

        InMemoryUserRepository.save(new User(account.get(), password.get(), email.get()));
        return HttpResponse.redirect(INDEX_PATH);
    }

    private HttpResponse resolveLoginResponse(final String requestBody, final Optional<Session> session) {
        final var loginUser = findLoginUser(requestBody);
        if (loginUser.isEmpty()) {
            return HttpResponse.redirect(UNAUTHORIZED_PATH);
        }

        final var user = loginUser.get();
        final var loginSession = session.orElseGet(sessionManager::createSession);
        loginSession.setAttribute(SESSION_USER_ATTRIBUTE, user);
        log.info("login user found: {}", user.getAccount());
        final var response = HttpResponse.redirect(INDEX_PATH);
        if (session.isPresent()) {
            return response;
        }
        return addSessionCookie(response, loginSession);
    }

    private HttpResponse resolveStaticResponse(final String path) {
        try {
            return HttpResponse.ok(responseContentResolver.resolve(path));
        } catch (HttpException e) {
            log.error(e.getMessage(), e);
            return HttpResponse.error(e.status());
        }
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        final var content = response.content();
        final var headerLines = new ArrayList<String>();
        headerLines.add("HTTP/1.1 " + response.status().code() + " " + response.status().reasonPhrase() + " ");
        for (final var header : response.headers()) {
            headerLines.add(header.name() + ": " + header.value() + " ");
        }
        headerLines.add("Content-Type: " + content.contentType() + " ");
        headerLines.add("Content-Length: " + content.body().length + " ");
        headerLines.add("");
        headerLines.add("");
        final var headers = String.join(CRLF, headerLines);

        outputStream.write(headers.getBytes(StandardCharsets.UTF_8));
        outputStream.write(content.body());
        outputStream.flush();
    }
}
