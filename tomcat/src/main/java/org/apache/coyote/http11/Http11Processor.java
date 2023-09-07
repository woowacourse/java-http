package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestFactory;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ACCOUNT_KEY = "account";
    private static final String PASSWORD_KEY = "password";
    private static final String EMAIL_KEY = "email";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String JSESSIONID_COOKIE_NAME = "JSESSIONID";
    private static final String USER_SESSION_ATTRIBUTE_NAME = "user";
    private static final SessionManager sessionManager = new SessionManager();

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
        try (final var outputStream = connection.getOutputStream();
             final var inputStream = connection.getInputStream();
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
             ) {
            final HttpRequest request = HttpRequestFactory.readFrom(bufferedReader);

            final HttpResponse response = handleRequest(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    public HttpResponse handleRequest(final HttpRequest request) throws URISyntaxException {

        final HttpResponse response = new HttpResponse();

        makeSessionIfNotExist(request, response);

        final String uriPath = request.getPath();

        if (uriPath.equals("/login")) {
            return handleLogin(request, response);
        }

        if (uriPath.equals("/register")) {
            return handleRegister(request, response);
        }

        response.hostingPage(uriPath);
        return response;
    }

    private void makeSessionIfNotExist(final HttpRequest request, final HttpResponse response) {
        final String sessionId = request.getCookie(JSESSIONID_COOKIE_NAME);
        if (sessionManager.findSession(sessionId) == null) {
            final UUID generatedSessionId = UUID.randomUUID();
            final Session session = new Session(generatedSessionId.toString());
            sessionManager.addSession(session);
            response.setCookie(JSESSIONID_COOKIE_NAME, session.getId());
        }
    }

    private HttpResponse handleLogin(final HttpRequest request, final HttpResponse response) {

        final Session session = sessionManager.findSession(request.getCookie(JSESSIONID_COOKIE_NAME));

        if (request.getMethod().equals("GET")) {
            if (session != null && session.getAttribute(USER_SESSION_ATTRIBUTE_NAME) instanceof User) {
                return response.redirectTo(INDEX_PAGE);
            }
            return response.hostingPage(LOGIN_PAGE);
        }

        if (request.getMethod().equals("POST")) {
            if (processLogin(request)) {
                final User user = InMemoryUserRepository.findByAccount(request.getBody(ACCOUNT_KEY)).get();
                session.setAttribute(USER_SESSION_ATTRIBUTE_NAME, user);
                return response.redirectTo(INDEX_PAGE);
            }
            return response.redirectTo(UNAUTHORIZED_PAGE);
        }
        return response.methodNotAllowed();
    }

    public boolean processLogin(final HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY)) {
            return false;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }

    private HttpResponse handleRegister(final HttpRequest request, final HttpResponse response) {

        final Session session = sessionManager.findSession(request.getCookie(JSESSIONID_COOKIE_NAME));

        if (request.getMethod().equals("GET")) {
            if (session != null && session.getAttribute(USER_SESSION_ATTRIBUTE_NAME) instanceof User) {
                return response.redirectTo(INDEX_PAGE);
            }
            return response.hostingPage(REGISTER_PAGE);
        }
        if (request.getMethod().equals("POST")) {
            final User registeredUser = processRegister(request);
            session.setAttribute(USER_SESSION_ATTRIBUTE_NAME, registeredUser);
            return response.redirectTo(INDEX_PAGE);
        }
        return response.methodNotAllowed();
    }

    public User processRegister(final HttpRequest request) {
        if (!request.containsBody(ACCOUNT_KEY) || !request.containsBody(PASSWORD_KEY) || !request.containsBody(EMAIL_KEY)) {
            return null;
        }
        final String account = request.getBody(ACCOUNT_KEY);
        final String password = request.getBody(PASSWORD_KEY);
        final String email = request.getBody(EMAIL_KEY);

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return null;
        }
        final User registeredUser = new User(account, password, email);
        InMemoryUserRepository.save(registeredUser);
        return registeredUser;
    }
}
