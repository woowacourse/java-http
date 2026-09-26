package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.FileNotFoundException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_URI = "/";
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String LOGIN = "/login";
    private static final String REGISTER = "/register";
    private static final String POST_METHOD = "POST";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER = "user";
    private static final String LOGIN_SUCCESS = "/index.html";
    private static final String LOGIN_FAILURE = "/401.html";
    private static final String DOT_HTML = ".html";
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

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
        try (final var inputStream = connection.getInputStream(); final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = new HttpRequest(inputStream);
            final String method = request.getMethod();
            String requestUri = request.getUri();
            final String version = request.getHttpVersion();
            final HttpResponse response = new HttpResponse(version);
            final String requestBody = request.getBody();
            final String sessionId = request.getCookie(JSESSIONID);
            final Session session = SESSION_MANAGER.findSession(sessionId);
            String setCookie = createSetCookieHeader(sessionId);

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            final String requestPath = extractRequestPath(requestUri);

            if (method.equals(POST_METHOD) && requestPath.equals(LOGIN)) {
                setLoginResponse(response, requestBody, sessionId, setCookie);
                response.write(outputStream);
                return;
            }

            if (method.equals(POST_METHOD) && requestPath.equals(REGISTER)) {
                saveUser(requestBody);
                setRedirectResponse(response, LOGIN_SUCCESS, setCookie);
                response.write(outputStream);
                return;
            }

            requestUri = requestPath;

            if (requestPath.equals(LOGIN) && isLoggedIn(session)) {
                setRedirectResponse(response, LOGIN_SUCCESS, "");
                response.write(outputStream);
                return;
            }

            if (requestUri.equals(LOGIN) || requestUri.equals(REGISTER)) {
                requestUri += DOT_HTML;
            }

            String contentType = getContentType(requestUri);

            if (!requestUri.equals(ROOT_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            setOkResponse(response, contentType, responseBody, setCookie);
            response.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void setOkResponse(final HttpResponse response, final String contentType, final byte[] responseBody,
                               final String setCookie) {
        response.setStatus(200, "OK ");
        if (!setCookie.isEmpty()) {
            response.setHeader(SET_COOKIE_HEADER, setCookie + " ");
        }
        response.setHeader("Content-Type", contentType + " ");
        response.setHeader("Content-Length", responseBody.length + " ");
        response.setBody(responseBody);
    }

    private void setRedirectResponse(final HttpResponse response, final String location, final String setCookie) {
        response.setStatus(302, "Found ");
        if (!setCookie.isEmpty()) {
            response.setHeader(SET_COOKIE_HEADER, setCookie + " ");
        }
        response.setHeader("Location", location + " ");
        response.setHeader("Content-Length", "0 ");
    }

    private String createSetCookieHeader(final String sessionId) {
        if (sessionId != null) {
            return "";
        }
        return JSESSIONID + "=" + UUID.randomUUID();
    }

    private void setLoginResponse(final HttpResponse response, final String requestBody,
                                  final String sessionId, final String setCookie) {
        final String location = getLoginRedirectionLocation(requestBody);
        String responseCookie = setCookie;

        if (location.equals(LOGIN_SUCCESS)) {
            final User user = findUser(requestBody);
            final Session loginSession = getOrCreateSession(sessionId);
            loginSession.setAttribute(USER, user);
            responseCookie = createSessionCookie(sessionId, loginSession);
        }

        setRedirectResponse(response, location, responseCookie);
    }

    private Session getOrCreateSession(final String sessionId) {
        final Session session = SESSION_MANAGER.findSession(sessionId);
        if (session != null) {
            return session;
        }
        return SESSION_MANAGER.createSession();
    }

    private String createSessionCookie(final String requestSessionId, final Session session) {
        if (requestSessionId != null && requestSessionId.equals(session.getId())) {
            return "";
        }
        return JSESSIONID + "=" + session.getId();
    }

    private boolean isLoggedIn(final Session session) {
        return getUser(session) != null;
    }

    private User getUser(final Session session) {
        if (session == null) {
            return null;
        }
        return (User) session.getAttribute(USER);
    }

    private String getResourcePath(final String path) {
        final URL resource = getClass().getClassLoader().getResource(path);
        if (resource == null) {
            throw new UncheckedServletException(new FileNotFoundException("리소스를 찾을 수 없습니다: " + path));
        }
        return resource.getPath();
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private boolean hasQueryString(final String requestUri) {
        return requestUri.indexOf('?') != -1;
    }

    private String extractRequestPath(final String requestUri) {
        if (!hasQueryString(requestUri)) {
            return requestUri;
        }

        return requestUri.substring(0, requestUri.indexOf('?'));
    }

    private String getLoginRedirectionLocation(final String requestBody) {
        final String[] formParts = requestBody.split("&");

        if (checkUser(formParts)) {
            return LOGIN_SUCCESS;
        }

        return LOGIN_FAILURE;
    }

    private User findUser(final String requestBody) {
        final Map<String, String> formData = parseFormData(requestBody);
        final String account = formData.get("account");
        final String password = formData.get("password");

        if (account == null || password == null) {
            return null;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return null;
        }
        return user.get();
    }

    private void saveUser(final String requestBody) {
        final Map<String, String> formData = parseFormData(requestBody);
        final User user = new User(formData.get("account"), formData.get("password"), formData.get("email"));
        InMemoryUserRepository.save(user);
    }

    private Map<String, String> parseFormData(final String requestBody) {
        final Map<String, String> formData = new HashMap<>();
        final String[] formFields = requestBody.split("&");

        for (String formField : formFields) {
            final String[] keyValue = formField.split("=", 2);
            if (keyValue.length < 2) {
                continue;
            }
            formData.put(keyValue[0], URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
        }
        return formData;
    }

    private boolean checkUser(final String[] requestParts) {
        if (requestParts.length < 2) {
            return false;
        }

        final String[] accountPart = requestParts[0].split("=", 2);
        final String[] passwordPart = requestParts[1].split("=", 2);
        if (accountPart.length < 2 || passwordPart.length < 2) {
            return false;
        }

        final String account = accountPart[1];
        final String password = passwordPart[1];

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return false;
        }

        if (user.get().checkPassword(password)) {
            log.info("로그인 성공: {}", user.get());
            return true;
        }

        return false;
    }
}
