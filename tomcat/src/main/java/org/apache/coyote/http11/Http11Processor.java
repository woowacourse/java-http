package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
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
            HttpResponse response = createResponse(request);
            response.writeTo(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(HttpRequest request) throws IOException {
        if (isRequest(request, HttpMethod.GET, "/register")) {
            return staticResourceResponse("/register.html");
        }
        if (isRequest(request, HttpMethod.POST, "/register")) {
            return register(request);
        }
        if (isRequest(request, HttpMethod.GET, "/login")) {
            return staticResourceResponse("/login.html");
        }
        if (isRequest(request, HttpMethod.POST, "/login")) {
            return login(request);
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

    private HttpResponse login(HttpRequest request) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (isAuthenticated(account, password)) {
            return HttpResponse.redirect("/index.html");
        }
        return HttpResponse.redirect("/401.html");
    }

    private boolean isAuthenticated(String account, String password) {
        if (account == null || password == null) {
            return false;
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return false;
        }
        return user.get().checkPassword(password);
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
