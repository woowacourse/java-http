package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpResponse response = new HttpResponse();

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
        ) {
            HttpRequest request = HttpRequestReader.read(inputStream);
            route(request);
            response.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void route(final HttpRequest request) throws IOException, URISyntaxException {
        if ("GET".equals(request.getMethod())) {
            handleGetMethod(request);
        }
        if("POST".equals(request.getMethod())) {
            handlePostMethod(request);
        }
    }

    private void handleGetMethod(HttpRequest request) throws IOException, URISyntaxException {
        if ("/".equals(request.getPath())) {
            response.ok("Hello world!", "text/html;charset=utf-8");
            return;
        }
        if ("/login".equals(request.getPath())) {
            if(isLoggedIn(request)) {
                response.found("/index.html");
                return;
            }
            response.ok("/login.html");
            return;
        }
        if("/register".equals(request.getPath())) {
            response.ok("/register.html");
            return;
        }
        response.ok(request.getPath());
    }

    private void handlePostMethod(HttpRequest request) throws IOException, URISyntaxException {
        if("/register".equals(request.getPath())) {
            handleRegister(request);
            return;
        }
        if("/login".equals(request.getPath())) {
            handleLogin(request);
            return;
        }
        response.found("/404.html");
    }

    private void handleRegister(HttpRequest request) {
        Map<String, String> requestBody = request.getRequestBody();
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);
        response.found("/index.html");
    }

    private void handleLogin(HttpRequest request) throws IOException, URISyntaxException {
        Map<String, String> requestBody = request.getRequestBody();
        if(requestBody.isEmpty()) {
            log.info("login failed: queryString is empty");
            response.found("/401.html");
            return;
        }
        if(isLoggedIn(request)) {
            log.info("login successful: already logged in");
            response.ok("/index.html");
            return;
        }
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if(userOptional.isPresent() && userOptional.get().checkPassword(requestBody.get("password"))) {
            loginInSession(userOptional.get());
            return;
        }
        log.info("login failed: invalid user info");
        response.found("/401.html");
    }

    private void loginInSession(final User user) {
        log.info("User{}", user);
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        session.setAttribute("user", user);
        response.addCookie("JSESSIONID=" + session.getId());
        response.found("/index.html");
        log.info("login successful: logged in");
        return;
    }

    private boolean isLoggedIn(HttpRequest request) {
        HttpCookie httpCookie = request.getHttpCookie();
        if(httpCookie.hasNoSession()) {
            return false;
        }
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(httpCookie.getSessionId());
        if(session == null) {
            return false;
        }
        User user = getUser(session);
        return user != null && InMemoryUserRepository.findByAccount(user.getAccount()).isPresent();
    }

    private User getUser(final Session session) {
        return (User) session.getAttribute("user");
    }
}
