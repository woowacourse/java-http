package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RequestHandler {

    private static final String JSESSIONID = "JSESSIONID";
    private static final String SESSION_USER_KEY = "user";

    public HttpResponse handle(final HttpRequest request) throws IOException, URISyntaxException {
        final HttpResponse response = new HttpResponse();
        final String path = request.getPath();
        if (path.equals("/")) {
            response.setBody("text/html", "Hello world!");
            return response;
        }

        if (path.equals("/login")) {
            handleLogin(request, response);
            return response;
        }

        if (path.equals("/register")) {
            handleRegister(request, response);
            return response;
        }

        final var resource = findResource(path);
        if (resource == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody("text/html", page("/404.html"));
            return response;
        }

        response.setBody(contentType(path), readResource(resource));
        return response;
    }

    private void handleLogin(final HttpRequest request, final HttpResponse response) throws IOException, URISyntaxException {
        if (request.getMethod() != HttpMethod.POST) {
            if (isLoggedIn(request)) {
                response.sendRedirect("/index.html");
                return;
            }
            response.setBody("text/html", page("/login.html"));
            return;
        }
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        if (isBlank(account) || isBlank(password)) {
            response.sendRedirect("/401.html");
            return;
        }

        final Optional<User> user = findUser(account, password);
        if (user.isEmpty()) {
            response.sendRedirect("/401.html");
            return;
        }

        loginSuccess(user.get(), response);
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final String SessionId = request.getCookie().get(JSESSIONID);
        final Session session = SessionManager.INSTANCE.findSession(SessionId);
        return session != null && session.getAttribute(SESSION_USER_KEY) != null;
    }

    private Optional<User> findUser(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void loginSuccess(final User user, final HttpResponse response) {
        final Session session = SessionManager.INSTANCE.createSession();
        session.setAttribute(SESSION_USER_KEY, user);

        response.sendRedirect("/index.html");
        response.addHeader("Set-Cookie", JSESSIONID + "=" + session.getId());
    }

    private void handleRegister(final HttpRequest request, final HttpResponse response) throws IOException, URISyntaxException {
        if (request.getMethod() != HttpMethod.POST) {
            response.setBody("text/html", page("/register.html"));
            return;
        }
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            response.setBody("text/html", page("/register.html"));
            return;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect("/index.html");
    }

    private boolean isBlank(final String value) {
        return value == null || value.isBlank();
    }

    private String readResource(final URL resource) throws IOException, URISyntaxException {
        return Files.readString(Path.of(resource.toURI()), UTF_8);
    }

    private URL findResource(final String path) {
        return getClass().getClassLoader().getResource("static" + path);
    }

    private String page(final String path) throws URISyntaxException, IOException {
        return readResource(findResource(path));
    }

    private String contentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }
}
