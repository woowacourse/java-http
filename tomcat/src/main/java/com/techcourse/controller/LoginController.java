package com.techcourse.controller;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.http11.HttpCookie;
import org.apache.coyote.http11.Session;
import org.apache.coyote.http11.SessionManager;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class LoginController extends AbstractController {

    private static final Logger log = LoggerFactory.getLogger(LoginController.class);

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";
    private static final String JSESSION_ID_KEY = "JSESSIONID";
    private static final String LOGIN_USER_KEY = "user";

    @Override
    protected void doGet(final HttpRequest request, final HttpResponse response) throws Exception {
        final HttpCookie httpCookie = getHttpCookie(request);
        if (isLoggedIn(httpCookie)) {
            redirect(response, "/index.html", httpCookie);
            return;
        }

        final URI uri = URI.create(request.getRequestLine().getTarget());
        handleLogin(response, uri.getQuery(), httpCookie);
    }

    @Override
    protected void doPost(final HttpRequest request, final HttpResponse response) throws Exception {
        handleLogin(response, request.getBody().getContent(), getHttpCookie(request));
    }

    private void handleLogin(final HttpResponse response, final String loginParameters,
                             final HttpCookie httpCookie) throws IOException {
        if (loginParameters == null) {
            final Path filePath = getFilePath("/login");
            response.set(HttpStatus.OK, filePath, Files.readString(filePath), null, httpCookie);
            return;
        }

        final Optional<User> user = authenticate(extractQueryParams(loginParameters));
        if (user.isPresent()) {
            saveUserInSession(httpCookie, user.get());
            redirect(response, "/index.html", httpCookie);
            return;
        }

        redirect(response, "/401.html", httpCookie);
    }

    private void redirect(final HttpResponse response, final String location,
                          final HttpCookie httpCookie) {
        response.set(HttpStatus.FOUND, getFilePath(location), "", location, httpCookie);
    }

    private HttpCookie getHttpCookie(final HttpRequest request) {
        return new HttpCookie(request.getHeaders().get("Cookie"));
    }

    private boolean isLoggedIn(final HttpCookie httpCookie) throws IOException {
        final String sessionId = httpCookie.get(JSESSION_ID_KEY);
        if (sessionId == null) {
            return false;
        }

        final Session session = SessionManager.getInstance().findSession(sessionId);
        return session != null && session.getAttribute(LOGIN_USER_KEY) != null;
    }

    private void saveUserInSession(final HttpCookie httpCookie, final User user) throws IOException {
        final SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = httpCookie.get(JSESSION_ID_KEY);
        Session session = null;

        if (sessionId != null) {
            session = sessionManager.findSession(sessionId);
        }

        if (session == null) {
            sessionId = UUID.randomUUID().toString();
            session = new Session(sessionId);
            sessionManager.add(session);
            httpCookie.put(JSESSION_ID_KEY, sessionId);
        }

        session.setAttribute(LOGIN_USER_KEY, user);
    }

    private Optional<User> authenticate(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            log.error("login error");
            return Optional.empty();
        }

        log.info("user : {}", user.get());
        return user;
    }

    private Map<String, String> extractQueryParams(final String query) {
        final String[] queryParams = query.split(QUERY_PARAM_DELIMITER);
        final Map<String, String> params = new HashMap<>();

        for (String queryParam : queryParams) {
            final String[] pair = queryParam.split(QUERY_PARAM_VALUE_DELIMITER, 2);
            final String key = pair[0];
            final String value = pair.length == 2 ? pair[1] : "";
            params.put(key, value);
        }
        return params;
    }

    private Path getFilePath(final String uriPath) {
        final String resourceName = "static/" + (uriPath.startsWith("/") ? uriPath.substring(1) : uriPath);
        return resolveResourcePath(resourceName);
    }

    private Path resolveResourcePath(final String name) {
        final URL url = getClass().getClassLoader().getResource(name);
        if (url == null) {
            return Path.of("/");
        }
        return Path.of(url.getPath());
    }
}
