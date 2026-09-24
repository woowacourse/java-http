package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

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
            final var request = new HttpRequest(inputStream);

            final var cookieHeader = request.getHeader("cookie");
            final var cookie = HttpCookie.parse(cookieHeader);

            Session session = null;
            String sessionIdToSet = null;
            final var sessionId = cookie.getValue("JSESSIONID");

            if (sessionId.isPresent()) {
                session = SessionManager.getInstance().findSession(sessionId.get());
            }

            if (session == null) {
                sessionIdToSet = UUID.randomUUID().toString();
                session = new Session(sessionIdToSet);
                SessionManager.getInstance().add(session);
            }

            final var method = request.getMethod();
            final var requestUri = request.getUri();

            final Map<String, String> requestParameters;
            if ("POST".equals(method)) {
                final var requestBody = request.getBody();
                if (requestBody.isEmpty()) {
                    requestParameters = Map.of();
                } else {
                    requestParameters = parseParameters(requestBody);
                }
            } else {
                requestParameters = parseQueryString(requestUri);
            }

            final var requestPath = request.getPath();
            final var contentType = determineContentType(requestPath);

            if (requestPath.equals("/")) {
                final var responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
                writeResponse(outputStream, responseBody, contentType, sessionIdToSet);
                return;
            }

            if (requestPath.equals("/login")) {
                handleLogin(method, requestParameters, outputStream, contentType, session, sessionIdToSet);
                return;
            }

            if (requestPath.equals("/register")) {
                handleRegister(method, requestParameters, outputStream, contentType, sessionIdToSet);
                return;
            }

            final var responseBody = readStaticResource(requestPath);
            writeResponse(outputStream, responseBody, contentType, sessionIdToSet);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleLogin(final String method, final Map<String, String> parameters, final OutputStream outputStream, final String contentType, final Session session, final String sessionIdToSet) throws IOException {
        if ("GET".equals(method)) {
            if (isLoggedIn(session)) {
                writeRedirectResponse(outputStream, "/index.html", sessionIdToSet);
                return;
            }

            final var responseBody = readStaticResource("/login.html");
            writeResponse(outputStream, responseBody, contentType, sessionIdToSet);
            return;
        }

        if ("POST".equals(method)) {
            final var authenticatedUser = findAuthenticatedUser(parameters);

            if (authenticatedUser.isEmpty()) {
                writeRedirectResponse(outputStream, "/401.html", sessionIdToSet);
                return;
            }

            session.setAttribute("user", authenticatedUser.get());

            writeRedirectResponse(outputStream, "/index.html", sessionIdToSet);
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + method);
    }

    private void handleRegister(final String method, final Map<String, String> parameters, final OutputStream outputStream, final String contentType, final String sessionIdToSet) throws IOException {
        if ("GET".equals(method)) {
            final var responseBody = readStaticResource("/register.html");
            writeResponse(outputStream, responseBody, contentType, sessionIdToSet);
            return;
        }

        if ("POST".equals(method)) {
            register(parameters);
            writeRedirectResponse(outputStream, "/index.html", sessionIdToSet);
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + method);
    }

    private byte[] readStaticResource(final String path) throws IOException {
        final var resourcePath = "static" + path;

        final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (resource == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다." + resourcePath);
        }

        try (resource) {
            return resource.readAllBytes();
        }
    }

    private void writeResponse(final OutputStream outputStream, final byte[] bytes, String contentType, final String sessionId) throws IOException {
        final var response = new HttpResponse(200, "OK", bytes);

        if (sessionId != null) {
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        }

        response.addHeader("Content-Type", contentType);
        response.addHeader("Content-Length", String.valueOf(bytes.length));
        response.writeTo(outputStream);
    }

    private void writeRedirectResponse(final OutputStream outputStream, final String location, final String sessionId) throws IOException {
        final var response = new HttpResponse(302, "Found", new byte[0]);
        response.addHeader("Location", location);

        if (sessionId != null) {
            response.addHeader("Set-Cookie", "JSESSIONID=" + sessionId);
        }

        response.writeTo(outputStream);
    }

    private String determineContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private Map<String, String> parseQueryString(final String uri) {
        final var queryIndex = uri.indexOf('?');

        if (queryIndex < 0 || queryIndex == uri.length() - 1) {
            return Map.of();
        }

        final var queryString = uri.substring(queryIndex + 1);

        return parseParameters(queryString);
    }

    private Map<String, String> parseParameters(final String parameters) {
        return Arrays.stream(parameters.split("&"))
                .map(this::parseParameter)
                .collect(Collectors.toMap(
                        pair -> decodeParameter(pair[0]),
                        pair -> decodeParameter(pair[1])
                ));
    }

    private String[] parseParameter(final String parameter) {
        final var pair = parameter.split("=", 2);

        if (pair.length != 2 || pair[0].isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청 파라미터입니다. " + parameter);
        }

        return pair;
    }

    private String decodeParameter(final String parameter) {
        return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
    }

    private Optional<User> findAuthenticatedUser(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private boolean isLoggedIn(final Session session) {
        return session.getAttribute("user") instanceof User;
    }

    private void register(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("회원가입 정보가 올바르지 않습니다.");
        }

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
