package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String NOT_FOUND_PAGE = "/404.html";

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            HttpRequest request = HttpRequest.from(reader);
            if (request == null) {
                return;
            }
            HttpResponse response = createResponse(request);

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(HttpRequest request) throws IOException {
        HttpResponse response = new HttpResponse();
        String method = request.getMethod();
        String path = request.getPath();
        String sessionId = request.getCookie("JSESSIONID");
        if (sessionId == null) {
            response.setHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
        }
        if ("POST".equals(method) && "/login".equals(path)) {
            login(request, response);
            return response;
        }
        if ("POST".equals(method) && "/register".equals(path)) {
            register(request, response);
            return response;
        }
        if ("GET".equals(method) && "/login".equals(path) && isLoggedIn(sessionId)) {
            response.sendRedirect(INDEX_PAGE);
            return response;
        }
        if ("/".equals(path)) {
            response.setBody(ContentType.HTML, "Hello world!");
            return response;
        }
        String resourcePath = path;
        if ("/login".equals(path)) {
            resourcePath = LOGIN_PAGE;
        }
        if ("/register".equals(path)) {
            resourcePath = REGISTER_PAGE;
        }
        String responseBody = readStaticResource(resourcePath);
        if (responseBody == null) {
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setBody(ContentType.HTML, readStaticResource(NOT_FOUND_PAGE));
            return response;
        }
        response.setBody(ContentType.from(resourcePath), responseBody);
        return response;
    }

    private String readStaticResource(String path) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream("static" + path)) {
            if (resource == null) {
                return null;
            }
            return new String(resource.readAllBytes(), StandardCharsets.UTF_8);
        }
    }

    private void login(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        if (account == null || password == null) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        if (loginUser.isEmpty()) {
            response.sendRedirect(UNAUTHORIZED_PAGE);
            return;
        }
        Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", loginUser.get());
        SessionManager.add(session);
        log.info("user : {}", loginUser.get().getAccount());
        response.setHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        response.sendRedirect(INDEX_PAGE);
    }

    private boolean isLoggedIn(String sessionId) {
        if (sessionId == null) {
            return false;
        }
        Session session = SessionManager.findSession(sessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private void register(HttpRequest request, HttpResponse response) {
        String account = request.getParameter("account");
        String password = request.getParameter("password");
        String email = request.getParameter("email");
        if (account == null || account.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            response.sendRedirect(REGISTER_PAGE);
            return;
        }
        InMemoryUserRepository.save(new User(account, password, email));
        response.sendRedirect(INDEX_PAGE);
    }
}
