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
import java.nio.charset.StandardCharsets;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String SESSION_COOKIE_KEY = "JSESSIONID";
    private static final String USER_ATTRIBUTE_KEY = "user";

    private final Socket connection;

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

            HttpRequest request = new HttpRequestParser(inputStream).parse();
            Cookies cookies = new Cookies(request.getHeader("Cookie"));
            Optional<Session> session = SessionManager.find(cookies.getValue(SESSION_COOKIE_KEY));

            HttpResponse response = createResponse(request, session);
            response.writeTo(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
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
        if (isRequest(request, HttpMethod.GET, "/register")) {
            return staticResourceResponse("/register.html");
        }
        if (isRequest(request, HttpMethod.POST, "/register")) {
            return register(request);
        }
        if (isRequest(request, HttpMethod.GET, "/login")) {
            if (session.isPresent() && session.get().getAttribute(USER_ATTRIBUTE_KEY) != null) {
                return HttpResponse.redirect("/index.html");
            }
            return staticResourceResponse("/login.html");
        }
        if (isRequest(request, HttpMethod.POST, "/login")) {
            return login(request, session);
        }
        return staticResourceResponse(request.getPath());
    }

    private boolean isRequest(HttpRequest request, HttpMethod method, String path) {
        return request.getMethod() == method && request.getPath().equals(path);
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
        String responseBody = readStaticResource(requestPath);
        return HttpResponse.ok(responseBody, contentType(requestPath));
    }

    private String readStaticResource(String requestPath) throws IOException {
        if (!requestPath.endsWith(".html") && !requestPath.endsWith(".css")) {
            return DEFAULT_RESPONSE_BODY;
        }

        try (InputStream resource = getClass().getResourceAsStream("/static" + requestPath)) {
            if (resource == null) {
                return DEFAULT_RESPONSE_BODY;
            }
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private String contentType(String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css";
        }
        return "text/html;charset=utf-8";
    }
}
