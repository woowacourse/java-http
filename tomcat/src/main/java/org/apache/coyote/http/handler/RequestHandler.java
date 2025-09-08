package org.apache.coyote.http.handler;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.nio.file.Files;
import java.nio.file.Path;
import org.apache.coyote.http.request.HttpRequest;
import org.apache.coyote.http.response.HttpResponse;

public class RequestHandler {

    public HttpResponse handleRequest(HttpRequest request) {
        return switch (request.getMethod() + " " + request.getEndpoint()) {
            case String s when s.equals("GET /") -> handleHome();
            case String s when s.equals("GET /css/styles.css") -> handleStaticFile("/css/styles.css", "text/css");
            case String s when s.startsWith("GET /login") -> handleStaticFile("/login.html", "text/html");
            case String s when s.startsWith("POST /login") -> handleLogin(request);
            case String s when s.startsWith("GET /register") -> handleStaticFile("/register.html", "text/html");
            case String s when s.startsWith("POST /register") -> handleRegister(request);
            default -> handleStaticFile(request.getEndpoint(), "text/html");
        };
    }

    private HttpResponse handleHome() {
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

    private HttpResponse handleLogin(HttpRequest request) {
        final var params = request.parseFormData();
        final var account = params.get("account");
        final var password = params.get("password");

        final var user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent() && user.get().checkPassword(password)) {
            return HttpResponse.redirect("/index.html");
        } else {
            try {
                final var path = Path.of(getClass().getResource("/static/login.html").getPath());
                final var content = new String(Files.readAllBytes(path));
                return HttpResponse.unauthorized(content);
            } catch (Exception e) {
                return HttpResponse.unauthorized("Login failed");
            }
        }
    }

    private HttpResponse handleRegister(HttpRequest request) {
        final var params = request.parseFormData();
        final var account = params.get("account");
        final var password = params.get("password");
        final var email = params.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.redirect("/index.html");
    }
}
