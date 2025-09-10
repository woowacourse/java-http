package org.apache.coyote.http.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.catalina.session.Session;
import org.apache.coyote.http.cookie.HttpCookie;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RequestHandler {
    
    public HttpResponse handleRequest(HttpRequest request) {
        return switch (request.getMethod() + " " + request.getEndpoint()) {
            case String s when s.equals("GET /") -> handleHome(request);
            case String s when s.equals("GET /css/styles.css") -> handleStaticFile("/css/styles.css", "text/css");
            case String s when s.startsWith("GET /login") -> handleLoginPage(request);
            case String s when s.startsWith("POST /login") -> handleLogin(request);
            case String s when s.startsWith("GET /register") -> handleStaticFile("/register.html", "text/html");
            case String s when s.startsWith("POST /register") -> handleRegister(request);
            default -> handleStaticFile(request.getEndpoint(), "text/html");
        };
    }

    private HttpResponse handleHome(HttpRequest request) {
        final var session = request.getSession(true);
        if (!request.getCookies().hasJSessionId()) {
            return HttpResponse.okWithCookie("Hello world!", "text/html", HttpCookie.JSESSIONID, session.getId());
        }
        return HttpResponse.ok("Hello world!", "text/html");
    }

    private HttpResponse handleStaticFile(String filePath, String contentType) {
        try {
            final var path = Path.of(getClass().getResource("/static" + filePath).getPath());
            final var content = new String(Files.readAllBytes(path));
            return HttpResponse.ok(content, contentType);
        } catch (Exception e) {
            return HttpResponse.ok("404 Not Found", "text/html");
        }
    }

    private HttpResponse handleLoginPage(HttpRequest request) {
        Session session = request.getSession(false);
        if (session != null && getUser(session) != null) {
            return HttpResponse.redirect("/index.html");
        }

        return handleStaticFile("/login.html", "text/html");
    }

    private HttpResponse handleLogin(HttpRequest request) {
        final var params = request.parseFormData();
        final var account = params.get("account");
        final var password = params.get("password");

        final var user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            final var sessionId = HttpCookie.generateJSessionId();
            return HttpResponse.redirectWithCookie("/index.html", HttpCookie.JSESSIONID, sessionId);
        } else {
            try {
                final var path = Path.of(getClass().getResource("/static/401.html").getPath());
                final var content = new String(Files.readAllBytes(path));
                return HttpResponse.unauthorized(content);
            } catch (Exception e) {
                return HttpResponse.unauthorized("401 Unauthorized");
            }
        }
    }

    private HttpResponse handleRegister(HttpRequest request) {
        final var params = request.parseFormData();
        final var account = params.get("account");
        final var password = params.get("password");
        final var email = params.get("email");

        if (account == null || password == null || email == null) {
            return handleStaticFile("/register.html", "text/html");
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            return handleStaticFile("/register.html", "text/html");
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        final var session = request.getSession(true);
        session.setAttribute("user", user);

        return HttpResponse.redirectWithCookie("/index.html", HttpCookie.JSESSIONID, session.getId());
    }

    private User getUser(Session session) {
        return (User) session.getAttribute("user");
    }
}
