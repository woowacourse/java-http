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
        final String path = request.getPath();
        if (path.equals("/")) {
            return HttpResponse.ok("text/html", "Hello world!");
        }

        if (path.equals("/login")) {
            return handleLogin(request);
        }

        if (path.equals("/register")) {
            return handleRegister(request);
        }

        final var resource = findResource(path);
        if (resource == null) {
            return HttpResponse.notFound("text/html", page("/404.html"));
        }

        return HttpResponse.ok(contentType(path), readResource(resource));
    }

    private HttpResponse handleLogin(final HttpRequest request) throws IOException, URISyntaxException {
        if (request.getMethod() != HttpMethod.POST) {
            if (isLoggedIn(request)) {
                return HttpResponse.redirect("/index.html");
            }
            return HttpResponse.ok("text/html", page("/login.html"));
        }
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        if (isBlank(account) || isBlank(password)) {
            return HttpResponse.redirect("/401.html");
        }

        final Optional<User> user = findUser(account, password);
        return user.map(this::loginSuccess)
                .orElseGet(() -> HttpResponse.redirect("/401.html"));
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

    private HttpResponse loginSuccess(final User user) {
        final Session session = SessionManager.INSTANCE.createSession();
        session.setAttribute(SESSION_USER_KEY, user);

        final HttpResponse response = HttpResponse.redirect("/index.html");
        response.addHeader("Set-Cookie", JSESSIONID + "=" + session.getId());
        return response;
    }

    private HttpResponse handleRegister(final HttpRequest request) throws IOException, URISyntaxException {
        if (request.getMethod() != HttpMethod.POST) {
            return HttpResponse.ok("text/html", page("/register.html"));
        }
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        final String email = request.getParameter("email");
        if (isBlank(account) || isBlank(password) || isBlank(email)) {
            return HttpResponse.ok("text/html", page("/register.html"));
        }
        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.redirect("/index.html");
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
