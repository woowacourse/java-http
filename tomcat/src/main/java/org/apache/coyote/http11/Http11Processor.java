package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String SESSION_COOKIE_KEY = "JSESSIONID";
    private static final String USER_ATTRIBUTE_KEY = "user";

    private final Socket connection;
    private final StaticResourceHandler resources = new StaticResourceHandler(getClass().getClassLoader());

    public Http11Processor(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {

            HttpResponse response = readResponse(inputStream);
            response.writeTo(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse readResponse(InputStream inputStream) throws IOException {
        HttpRequest request;
        try {
            request = new HttpRequestParser(inputStream).parse();
        } catch (IllegalArgumentException e) {
            return resources.error(HttpStatus.BAD_REQUEST);
        }
        Cookies cookies = new Cookies(request.getHeader("Cookie"));
        Optional<Session> session = SessionManager.find(cookies.getValue(SESSION_COOKIE_KEY));
        return createResponseSafely(request, session);
    }

    private HttpResponse createResponseSafely(HttpRequest request, Optional<Session> session) {
        try {
            return createResponse(request, session);
        } catch (IOException | RuntimeException e) {
            log.error(e.getMessage(), e);
            return resources.error(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private void addSessionCookie(HttpResponse response, Session session) {
        HttpCookie sessionCookie = new HttpCookie(
                SESSION_COOKIE_KEY,
                session.getId()
        );
        response.addHeader("Set-Cookie", sessionCookie.toHeaderValue());
    }

    private HttpResponse createResponse(HttpRequest request, Optional<Session> session) throws IOException {
        return switch (Route.find(request.getMethod(), request.getPath())) {
            case HOME -> staticResourceResponse("/index.html");
            case REGISTER_PAGE -> staticResourceResponse("/register.html");
            case REGISTER -> register(request);
            case LOGIN_PAGE -> loginPage(session);
            case LOGIN -> login(request, session);
            case STATIC_RESOURCE -> staticResourceResponse(request.getPath());
            case NOT_FOUND -> resources.error(HttpStatus.NOT_FOUND);
        };
    }

    private HttpResponse loginPage(Optional<Session> session) throws IOException {
        if (session.isPresent() && session.get().getAttribute(USER_ATTRIBUTE_KEY) != null) {
            return HttpResponse.redirect("/index.html");
        }
        return staticResourceResponse("/login.html");
    }

    private HttpResponse register(HttpRequest request) {
        User user = new User(
                request.getParameter("account"),
                request.getParameter("password"),
                request.getParameter("email")
        );
        InMemoryUserRepository.save(user);
        return HttpResponse.redirect("/index.html");
    }

    private HttpResponse login(HttpRequest request, Optional<Session> existingSession) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        Optional<User> user = findAuthenticatedUser(account, password);
        if (user.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }

        HttpResponse response = HttpResponse.redirect("/index.html");
        Session session = existingSession.orElseGet(SessionManager::create);
        session.setAttribute(USER_ATTRIBUTE_KEY, user.get());
        if (existingSession.isEmpty()) {
            addSessionCookie(response, session);
        }
        return response;
    }

    private Optional<User> findAuthenticatedUser(String account, String password) {
        if (account == null || password == null) {
            return Optional.empty();
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return Optional.empty();
        }
        if (!user.get().checkPassword(password)) {
            return Optional.empty();
        }
        return user;
    }

    private HttpResponse staticResourceResponse(String requestPath) throws IOException {
        return resources.respond(requestPath);
    }
}
