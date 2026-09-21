package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String DEFAULT_BODY = "Hello world!";
    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String LOGIN_PATH = "/login";
    private static final String ROOT_PATH = "/";
    private static final String INDEX_PAGE = "/index.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String REGISTER_PATH = "/register";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER_ATTRIBUTE = "user";

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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            final HttpRequest request = HttpRequest.from(bufferedReader);
            final HttpResponse response = createResponse(request);

            outputStream.write(response.toMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(final HttpRequest request) throws IOException {
        final HttpCookie cookie = request.getCookie();
        if (request.isEmpty()) {
            return addSessionCookie(HttpResponse.of(HttpStatus.OK, DEFAULT_CONTENT_TYPE, DEFAULT_BODY), cookie);
        }
        if (request.isPost(REGISTER_PATH)) {
            return addSessionCookie(createRegisterResponse(request), cookie);
        }
        if (request.isPost(LOGIN_PATH)) {
            return createLoginResponse(request);
        }
        if (request.isGet(LOGIN_PATH) && isLoggedIn(cookie)) {
            return HttpResponse.redirect(INDEX_PAGE);
        }

        return addSessionCookie(createResourceResponse(request.getPath()), cookie);
    }

    private HttpResponse addSessionCookie(final HttpResponse response, final HttpCookie cookie) {
        if (!cookie.hasJSessionId()) {
            response.setCookie(JSESSIONID + "=" + UUID.randomUUID());
        }
        return response;
    }

    private boolean isLoggedIn(final HttpCookie cookie) {
        if (!cookie.hasJSessionId()) {
            return false;
        }
        final Session session = SessionManager.getInstance().findSession(cookie.getJSessionId());
        return session != null && session.getAttribute(USER_ATTRIBUTE) != null;
    }

    private HttpResponse createLoginResponse(final HttpRequest request) {
        final Optional<User> user = login(request);
        if (user.isEmpty()) {
            return HttpResponse.redirect(UNAUTHORIZED_PAGE);
        }
        final Session session = createSession(user.get());
        final HttpResponse response = HttpResponse.redirect(INDEX_PAGE);
        response.setCookie(JSESSIONID + "=" + session.getId());
        return response;
    }

    private Session createSession(final User user) {
        final Session session = new Session(UUID.randomUUID().toString());
        session.setAttribute(USER_ATTRIBUTE, user);
        SessionManager.getInstance().add(session);
        return session;
    }

    private HttpResponse createRegisterResponse(final HttpRequest request) {
        register(request);
        return HttpResponse.redirect(INDEX_PAGE);
    }

    private Optional<User> login(final HttpRequest request) {
        final Map<String, String> params = request.getBodyParams();
        final Optional<User> user = InMemoryUserRepository.findByAccount(params.get("account"))
                .filter(it -> it.checkPassword(params.get("password")));
        user.ifPresent(it -> log.info("{}", it));
        return user;
    }

    private void register(final HttpRequest request) {
        final Map<String, String> params = request.getBodyParams();
        final User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email"));
        InMemoryUserRepository.save(user);
        log.info("회원가입: {}", user);
    }

    private HttpResponse createResourceResponse(final String path) throws IOException {
        if (path.equals(ROOT_PATH)) {
            return HttpResponse.of(HttpStatus.OK, DEFAULT_CONTENT_TYPE, DEFAULT_BODY);
        }

        final String resourcePath = toResourcePath(path);
        final URL resource = getClass().getClassLoader().getResource(resourcePath);
        if (resource == null) {
            final URL notFound = getClass().getClassLoader().getResource(NOT_FOUND_PAGE);
            return HttpResponse.of(HttpStatus.NOT_FOUND, DEFAULT_CONTENT_TYPE, readResource(notFound));
        }

        return HttpResponse.of(HttpStatus.OK, getContentType(resourcePath), readResource(resource));
    }

    private String getContentType(final String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript";
        }
        return DEFAULT_CONTENT_TYPE;
    }

    private String readResource(final URL resource) throws IOException {
        return Files.readString(Path.of(resource.getFile()));
    }

    private String toResourcePath(final String path) {
        if (path.contains(".")) {
            return "static" + path;
        }
        return "static" + path + ".html";
    }

}