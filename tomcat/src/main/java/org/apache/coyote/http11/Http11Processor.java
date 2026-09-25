package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.ContentType;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String BAD_REQUEST_MESSAGE = "잘못된 요청입니다.";

    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String INDEX_PATH = "/index.html";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String REGISTER_PATH = "/register";
    private static final String HTML_EXTENSION = ".html";
    private static final String STATIC_PREFIX = "static";

    private final Socket connection;

    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpResponse response = new HttpResponse();
            try {
                final HttpRequest request = HttpRequest.from(reader);
                if (request == null) {
                    return;
                }
                createResponse(request, response);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 요청입니다. {}", e.getMessage());
                response.setStatus(HttpStatus.BAD_REQUEST);
                response.setContentType(ContentType.HTML);
                response.setBody(BAD_REQUEST_MESSAGE);
            }

            outputStream.write(response.getResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void createResponse(final HttpRequest request, final HttpResponse response) throws IOException {
        final String requestPath = request.getPath();

        if (request.isPost()) {
            if (LOGIN_PATH.equals(requestPath)) {
                final Optional<User> user = login(request);
                if (user.isPresent()) {
                    log.info("로그인 성공: {}", user.get());
                    final String sessionId = UUID.randomUUID().toString();
                    final Session session = new Session(sessionId);
                    session.setAttribute("user", user.get());
                    SESSION_MANAGER.add(session);

                    response.sendRedirect(INDEX_PATH);
                    response.addJSessionId(sessionId);
                    return;
                }
                log.info("로그인 실패: {}", request.getParameter("account"));
                response.sendRedirect(UNAUTHORIZED_PATH);
                return;
            }

            if (REGISTER_PATH.equals(requestPath)) {
                final String account = request.getParameter("account");
                final String password = request.getParameter(("password"));
                final String email = request.getParameter(("email"));
                if (account == null || password == null || email == null) {
                    response.setStatus(HttpStatus.BAD_REQUEST);
                    response.setContentType(ContentType.HTML);
                    response.setBody(BAD_REQUEST_MESSAGE);
                    return;
                }
                InMemoryUserRepository.save(new User(account, password, email));
                log.info("회원가입 성공: {}", account);
                response.sendRedirect(INDEX_PATH);
                return;
            }
        }

        if (LOGIN_PATH.equals(requestPath)) {
            final String sessionId = request.getCookie().getJsessionid();
            if (sessionId != null) {
                final Session session = SESSION_MANAGER.findSession(sessionId);
                if (session != null) {
                    log.info("이미 로그인된 사용자: {}", SESSION_MANAGER.findSession(sessionId).getAttribute("user"));
                    response.sendRedirect(INDEX_PATH);
                    return;
                }
            }
        }

        try {
            response.setContentType(ContentType.from(requestPath));
            response.setBody(resolveResponseBody(requestPath));
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            response.setStatus(HttpStatus.NOT_FOUND);
            response.setContentType(ContentType.HTML);
            response.setBody(resolveResponseBody(NOT_FOUND_PATH));
        }
    }

    private Optional<User> login(final HttpRequest request) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        if (account == null || password == null) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private String resolveResponseBody(String requestPath) throws IOException {
        if (ROOT_PATH.equals(requestPath)) {
            return DEFAULT_MESSAGE;
        }
        if (!requestPath.contains(".")) {
            requestPath += HTML_EXTENSION;
        }
        return readStaticFile(requestPath);
    }

    private String readStaticFile(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(STATIC_PREFIX + resourcePath);
        if (resource == null) {
            throw new RuntimeException("요청한 리소스를 찾을 수 없습니다: " + resourcePath);
        }

        try {
            final Path path = Path.of(resource.toURI());

            if (!Files.isRegularFile(path)) {
                throw new RuntimeException("요청한 리소스를 찾을 수 없습니다: " + resourcePath);
            }
            return Files.readString(path);
        } catch (URISyntaxException e) {
            throw new IOException("잘못된 리소스 경로입니다: " + resourcePath, e);
        }
    }
}
