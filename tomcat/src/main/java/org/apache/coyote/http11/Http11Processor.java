package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequestParser;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SimpleSessionManager sessionManager;

    public Http11Processor(final Socket connection, final SimpleSessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        process(connection);
    }

    @Override
    public void process(final Socket connection) {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest request = HttpRequestParser.parse(inputStream);
            attachExistingSession(request);

            final HttpResponse response = handleRequest(request);

            applySessionCookie(request, response);

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) throws IOException {
        if (request.isMatched(HttpMethod.GET, "/login")) {
            return handleLoginPage(request);
        }

        if (request.isMatched(HttpMethod.POST, "/login")) {
            return handleLogin(request);
        }

        if (request.isMatched(HttpMethod.GET, "/register")) {
            return createStaticResourceResponse(request, "/register.html");
        }

        if (request.isMatched(HttpMethod.POST, "/register")) {
            return handleRegister(request);
        }

        if (request.isMatched(HttpMethod.GET, "/")) {
            return createRootResponse(request);
        }

        if (request.isGet() && isStaticResource(request.getPath())) {
            return createStaticResourceResponse(request, request.getPath());
        }

        return createNotFoundResponse(request);
    }

    private HttpResponse handleLoginPage(HttpRequest request) throws IOException {
        if (isLoggedIn(request)) {
            return HttpResponse.redirect(request.getVersion(), "/index.html");
        }

        return createStaticResourceResponse(request, "/login.html");
    }

    private boolean isLoggedIn(HttpRequest request) {
        HttpSession session = request.getSession();
        return session != null && session.getAttribute("user") != null;
    }

    private HttpResponse handleLogin(HttpRequest request) {
        final String account = request.getBodyParamValue("account");
        final String password = request.getBodyParamValue("password");

        Optional<User> authenticatedUser = authenticate(account, password);

        if (authenticatedUser.isEmpty()) {
            return HttpResponse.redirect(request.getVersion(), "/401.html");
        }

        User user = authenticatedUser.get();
        HttpSession session = getOrCreateSession(request);
        session.setAttribute("user", user);

        log.info(user.toString());

        return HttpResponse.redirect(request.getVersion(), "/index.html");
    }

    private Optional<User> authenticate(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private HttpSession getOrCreateSession(HttpRequest request) {
        HttpSession session = request.getSession();
        if (session != null) {
            return session;
        }

        SimpleSession newSession = SimpleSession.create();
        sessionManager.add(newSession);
        request.setSession(newSession);
        return newSession;
    }

    private HttpResponse handleRegister(HttpRequest request) {
        String account = request.getBodyParamValue("account");
        String email = request.getBodyParamValue("email");
        String password = request.getBodyParamValue("password");

        saveUser(account, email, password);

        return HttpResponse.redirect(request.getVersion(), "/index.html");
    }

    private void saveUser(String account, String email, String password) {
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 account 입니다: " + account);
        }

        InMemoryUserRepository.save(new User(account, password, email));
        log.info("회원가입 완료: {}", account);
    }

    private HttpResponse createRootResponse(HttpRequest request) {
        byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
        return HttpResponse.ok(request.getVersion(), "text/html;charset=utf-8", body);
    }

    private HttpResponse createStaticResourceResponse(HttpRequest request, String path) throws IOException {
        String resourcePath = "static" + path;

        byte[] body = readResourceBytes(resourcePath);
        String contentType = resolveContentType(path);

        return HttpResponse.ok(request.getVersion(), contentType, body);
    }

    private HttpResponse createNotFoundResponse(HttpRequest request) throws IOException {
        byte[] body = readResourceBytes("static/404.html");
        return HttpResponse.notFound(request.getVersion(), body);
    }

    private boolean isStaticResource(String path) {
        return path.endsWith(".html")
                || path.endsWith(".css")
                || path.endsWith(".js");
    }

    private byte[] readResourceBytes(String path) throws IOException {
        try (final var fileStream = getClass().getClassLoader().getResourceAsStream(path)) {
            if (fileStream == null) {
                throw new RuntimeException(path + "을 찾을 수 없습니다.");
            }
            return fileStream.readAllBytes();
        }
    }

    private String resolveContentType(String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8";
        }

        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }

        return "application/octet-stream";
    }

    private void attachExistingSession(HttpRequest request) {
        request.getSessionId()
                .map(sessionManager::findSession)
                .ifPresent(request::setSession);
    }

    private void applySessionCookie(HttpRequest request, HttpResponse response) {
        HttpSession session = request.getSession();
        if (session == null || !session.isNew()) {
            return;
        }

        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId() + "; Path=/");
        if (session instanceof SimpleSession simpleSession) {
            simpleSession.markEstablished();
        }
    }

    private void writeResponse(
            OutputStream outputStream,
            HttpResponse response
    ) throws IOException {
        response.writeTo(outputStream);
        outputStream.flush();
    }
}
