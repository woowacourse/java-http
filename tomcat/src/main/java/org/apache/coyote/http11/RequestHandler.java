package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

import static java.nio.charset.StandardCharsets.UTF_8;

public class RequestHandler {

    public HttpResponse handle(final HttpRequest request) throws IOException, URISyntaxException {
        final String path = request.getPath();
        if (path.equals("/")) {
            return HttpResponse.ok("text/html", "Hello world!");
        }

        if (path.equals("/login")) {
            return handleLogin(request);
        }

        if (path.equals("/register")) {
            return HttpResponse.ok("text/html", readResource(findResource("/register.html")));
        }

        final var resource = findResource(path);
        if (resource == null) {
            return HttpResponse.notFound("text/html", readResource(findResource("/404.html")));
        }

        return HttpResponse.ok(contentType(path), readResource(resource));
    }

    private HttpResponse handleLogin(final HttpRequest request) throws IOException, URISyntaxException {
        final String account = request.getQueryParameter("account");
        final String password = request.getQueryParameter("password");
        if (!isLoginAttempt(account, password)) {
            return HttpResponse.ok("text/html", readResource(findResource("/login.html")));
        }
        if (isValidUser(account, password)) {
            return HttpResponse.redirect("/index.html");
        }
        return HttpResponse.redirect("/401.html");
    }

    private boolean isLoginAttempt(final String account, final String password) {
        return account != null && password != null;
    }

    private boolean isValidUser(final String account, final String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private String readResource(final URL resource) throws IOException, URISyntaxException {
        return Files.readString(Path.of(resource.toURI()), UTF_8);
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

    private URL findResource(final String path) {
        return getClass().getClassLoader().getResource("static" + path);
    }
}
