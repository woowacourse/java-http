package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             BufferedReader reader = new BufferedReader(
                     new InputStreamReader(connection.getInputStream()))
             ) {

            HttpRequest request = HttpRequest.from(reader);
            HttpResponse response;
            if (isLoginPageRequest(request) && isLoggedIn(request)) {
                response = HttpResponse.found("/index.html");
            } else if (isLoginRequest(request)) {
                response = handleLogin(request);
            } else if (isRegisterRequest(request)) {
                response = handleRegister(request);
            } else {
                String contentType = resolveContentType(request.getPath());
                String responseBody = resolveResponseBody(request.getPath());

                response = HttpResponse.ok(contentType, responseBody);
            }
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

    private String resolveResponseBody(String path) throws IOException {
        if (path.equals("/")) {
            return "Hello world!";
        }

        String resourcePath = "static" + path;
        if (!resourcePath.contains(".")) {
            resourcePath += ".html";
        }

        URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            return "404 Not Found";
        }

        return new String(Files.readAllBytes(Path.of(resource.getPath())));
    }

    private boolean isLoginRequest(HttpRequest request) {
        return request.getPath().equals("/login")
        && request.getMethod().equals("POST");
    }

    private boolean isLoginPageRequest(HttpRequest request) {
        return request.getPath().equals("/login")
                && request.getMethod().equals("GET");
    }

    private boolean isLoggedIn(HttpRequest request) {
        Session session = request.getSession(false);
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse handleLogin(HttpRequest request) {
        Map<String, String> parameters = request.getParameters();
        if (canLogin(parameters)) {
            User user = InMemoryUserRepository.findByAccount(parameters.get("account")).get();
            Session session = request.getSession(true);
            session.setAttribute("user", user);

            HttpResponse response = HttpResponse.found("/index.html");
            response.addCookie(session.getId());
            return response;
        }
        return HttpResponse.found("/401.html");
    }

    private boolean isRegisterRequest(HttpRequest request) {
        return request.getPath().equals("/register")
                && request.getMethod().equals("POST");
    }

    private HttpResponse handleRegister(HttpRequest request) {
        Map<String, String> parameters = request.getParameters();
        String account = parameters.get("account");
        String password = parameters.get("password");
        String email = parameters.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return HttpResponse.found("/index.html");
    }

    private boolean canLogin(Map<String, String> parameters) {
        String account = parameters.get("account");
        String password = parameters.get("password");
        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }
}
