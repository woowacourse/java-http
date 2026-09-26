package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String NOT_FOUND_PAGE = "/404.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String EMAIL_PARAMETER = "email";

    private static final String ACCEPT_HEADER = "Accept";
    private static final String COOKIE_HEADER = "Cookie";
    private static final String SET_COOKIE_HEADER = "Set-Cookie";
    private static final String CONTENT_TYPE_HEADER = "Content-Type";
    private static final String TEXT_HTML = "text/html;charset=utf-8";
    private static final String TEXT_CSS = "text/css";

    private static final String POST = "POST";
    private static final String GET = "GET";

    private static final String USER_ATTRIBUTE = "user";

    private final Socket connection;
    private final Manager sessionManager;

    public Http11Processor(final Socket connection, final Manager sessionManager) {
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
             final var inputStreamReader = new InputStreamReader(inputStream);
             final var bufferedReader = new BufferedReader(inputStreamReader);
             final var outputStream = connection.getOutputStream()) {

            final var request = HttpRequest.from(bufferedReader);
            if (request == null) {
                return;
            }
            log.info("{}", request.toString());

            final var cookie = new HttpCookie(request.getHeader(COOKIE_HEADER));
            var setCookie = "";
            if (!cookie.hasJSessionId()) {
                setCookie = HttpCookie.JSESSIONID + "=" + UUID.randomUUID();
            }
            final var contentType = decideContentType(request.getHeader(ACCEPT_HEADER));
            final var method = request.getMethod();
            final var path = request.getPath();

            final var response = new HttpResponse();
            response.setHeader(CONTENT_TYPE_HEADER, contentType);

            if (path.equals("/")) { // Hello World
                response.setBody("Hello world!".getBytes());
            } else if (method.equals(POST) && path.equals(REGISTER_PATH)) { // 회원가입
                if (register(request.getParameters())) {
                    response.sendRedirect(INDEX_PAGE);
                } else {
                    response.sendRedirect(REGISTER_PATH);
                }
            } else if (method.equals(POST) && path.equals(LOGIN_PATH)) {  // 로그인
                final var loginUser = login(request.getParameters());
                if (loginUser.isPresent()) {
                    final var session = sessionManager.createSession();
                    session.setAttribute(USER_ATTRIBUTE, loginUser.get());
                    setCookie = HttpCookie.JSESSIONID + "=" + session.getId();
                    response.sendRedirect(INDEX_PAGE);
                    log.info("Session id: {} -> {}", session.getId(), session.getAttribute(USER_ATTRIBUTE));
                } else {
                    response.sendRedirect(UNAUTHORIZED_PAGE);
                }
            } else if (method.equals(GET) && path.equals(LOGIN_PATH) && isLoggedIn(cookie)) { // GET 로그인
                response.sendRedirect(INDEX_PAGE);
            } else {
                var resourceUrl = findResource(path);
                if (resourceUrl == null) {
                    response.setStatus(HttpStatus.NOT_FOUND);
                    resourceUrl = findResource(NOT_FOUND_PAGE);
                }
                response.setBody(Files.readAllBytes(Path.of(resourceUrl.toURI())));
            }
            if (!setCookie.isEmpty()) {
                response.setHeader(SET_COOKIE_HEADER, setCookie);
            }
            log.info("{}",response);
            outputStream.write(response.build().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean isLoggedIn(HttpCookie cookie) throws IOException {
        if (!cookie.hasJSessionId()) {
            return false;
        }
        final var jSessionId = cookie.getJSessionId();
        final var session = sessionManager.findSession(jSessionId);
        if (session == null) {
            return false;
        }
        return session.getAttribute(USER_ATTRIBUTE) != null;
    }

    private String decideContentType(final String accept) {
        if (accept != null && accept.contains(TEXT_CSS)) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }

    private boolean register(final Map<String, String> formParameters) {
        final var account = formParameters.get(ACCOUNT_PARAMETER);
        final var password = formParameters.get(PASSWORD_PARAMETER);
        final var email = formParameters.get(EMAIL_PARAMETER);
        if (account == null || password == null || email == null) {
            return false;
        }
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        log.info("register success: {}", user);
        return true;
    }

    private Optional<User> login(final Map<String, String> formParameters) {
        final var account = formParameters.get(ACCOUNT_PARAMETER);
        final var password = formParameters.get(PASSWORD_PARAMETER);
        if (account == null || password == null) {
            return Optional.empty();
        }
        final var user = InMemoryUserRepository.findByAccount(account)
                .filter(found -> found.checkPassword(password));
        user.ifPresent(found -> log.info("login success: {}", found));
        return user;
    }

    private URL findResource(final String path) {
        if (path.contains(".")) {
            return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path);
        }
        return getClass().getClassLoader().getResource(STATIC_RESOURCE_ROOT + path + ".html");
    }
}
