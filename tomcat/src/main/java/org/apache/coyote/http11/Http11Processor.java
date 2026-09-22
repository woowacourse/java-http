package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final byte[] HELLO_WORLD = "Hello world!".getBytes(StandardCharsets.UTF_8);
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";

    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_SESSION_KEY = "user";

    private final Socket connection;
    private static final SessionManager SESSION_MANAGER =
            SessionManager.getInstance();

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
        try (final BufferedInputStream inputStream =
                     new BufferedInputStream(connection.getInputStream());
             final OutputStream outputStream = connection.getOutputStream()) {

            final Optional<HttpRequest> optionalRequest = HttpRequest.from(inputStream);

            if (optionalRequest.isEmpty()) {
                return;
            }
            final HttpRequest request = optionalRequest.get();
            final HttpResponse response = new HttpResponse();

            final String sessionId = resolveSessionId(request, response);

            boolean handled = handleLogin(request, sessionId, response);

            if (!handled) {
                handled = handleRegister(request, response);
            }

            if (!handled) {
                handleResource(request, response);
            }
            response.writeTo(outputStream);
        } catch (IOException
                 | URISyntaxException
                 | UncheckedServletException e) {

            log.error(e.getMessage(), e);
        }
    }

    private String resolveSessionId(
            final HttpRequest request,
            final HttpResponse response
    ) {

        final Optional<String> existingSessionId = request.getCookie(JSESSIONID);

        if (existingSessionId.isPresent() && !existingSessionId.get().isBlank()) {
            return existingSessionId.get();
        }

        final String newSessionId = UUID.randomUUID().toString();
        response.addHeader(SET_COOKIE, JSESSIONID + "=" + newSessionId);
        return newSessionId;
    }

    private boolean handleLogin(
            final HttpRequest request,
            final String sessionId,
            final HttpResponse response
    ) {
        if (!LOGIN_PATH.equals(request.getPath())) {
            return false;
        }

        if (GET.equals(request.getMethod())) {// 이미 로그인한 사용자가 GET /login
            final HttpSession session = SESSION_MANAGER.findSession(sessionId);

            if (session != null && getUser(session) != null) {
                response.sendRedirect("/index.html");
                return true;
            }
            // Session이 없거나 로그인하지 않았다면 새로 만들지 않고 login.html을 보여준다.
            return false;
        }

        if (!POST.equals(request.getMethod())) {
            return false;
        }

        final String account = request.getParameter("account").orElse(null);

        final String password = request.getParameter("password").orElse(null);

        if (account == null || password == null) {
            response.sendRedirect("/401.html");
            return true;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(foundUser -> foundUser.checkPassword(password));

        if (user.isEmpty()) {
            log.info("login failed account: {}", account);
            response.sendRedirect("/401.html");
            return true;
        }
        final User loginUser = user.get();

        // 로그인에 성공했을 때에만 Session을 생성한다.
        final HttpSession session = SESSION_MANAGER.createSession();
        // 서버 Session에 로그인 User 저장
        session.setAttribute(USER_SESSION_KEY, loginUser);
        response.addHeader(SET_COOKIE, JSESSIONID + "=" + session.getId());
        log.info("login success account: {}", loginUser.getAccount());
        response.sendRedirect("/index.html");
        return true;

    }

    private User getUser(final HttpSession session) {
        final Object value = session.getAttribute(USER_SESSION_KEY);
        if (value instanceof User user) {
            return user;
        }
        return null;
    }

    private boolean handleRegister(final HttpRequest request, final HttpResponse response) {
        if (!REGISTER_PATH.equals(request.getPath())) {
            return false;
        }

        if (GET.equals(request.getMethod())) {
            return false;
        }

        if (!POST.equals(request.getMethod())) {
            return false;
        }

        final String account = request.getParameter("account").orElse(null);

        final String password = request.getParameter("password").orElse(null);

        final String email = request.getParameter("email").orElse(null);

        if (account == null || password == null || email == null) {
            return false;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        log.info("register success account: {}", account);

        response.sendRedirect("/index.html");
        return true;
    }

    private void handleResource(
            final HttpRequest request,
            final HttpResponse response
    ) throws IOException, URISyntaxException {
        if ("/".equals(request.getPath())) {
            response.ok("text/html;charset=utf-8", HELLO_WORLD);
            return;
        }

        setStaticResourceResponse(response, request.getPath());
    }

    private void setStaticResourceResponse(
            final HttpResponse response,
            final String path
    ) throws IOException, URISyntaxException {
        final String resourcePath = resolveResourcePath(path);
        final URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (resource == null) {
            setNotFoundResponse(response);
            return;
        }

        final byte[] responseBody = Files.readAllBytes(Path.of(resource.toURI()));
        response.ok(resolveContentType(path), responseBody);
    }

    private void setNotFoundResponse(final HttpResponse response) {
        final byte[] responseBody = "Not Found".getBytes(StandardCharsets.UTF_8);
        response.notFound("text/plain;charset=utf-8", responseBody);
    }

    private String resolveResourcePath(final String path) {
        if ("/login".equals(path)) {
            return "static/login.html";
        }
        if (REGISTER_PATH.equals(path)) {
            return "static/register.html";
        }

        return "static" + path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }
}

