package org.apache.catalina;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.RequestHandler;
import org.apache.coyote.http11.HttpRequest;
import org.apache.coyote.http11.HttpResponse;
import org.apache.coyote.http11.HttpStatusLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Dispatcher implements RequestHandler {

    private static final Logger log = LoggerFactory.getLogger(Dispatcher.class);

    private static final String HTTP_VERSION = "HTTP/1.1";
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String LOCATION = "Location";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String USER = "user";

    private static final String ACCOUNT = "account";
    private static final String PASSWORD = "password";
    private static final String EMAIL = "email";

    private static final String INDEX_PAGE = "/index.html";
    private static final String LOGIN_PAGE = "/login.html";
    private static final String REGISTER_PAGE = "/register.html";
    private static final String UNAUTHORIZED_PAGE = "/401.html";
    private static final String NOT_FOUND_PAGE = "static/404.html";
    private static final String SERVER_ERROR_PAGE = "static/500.html";

    private final SessionManager sessionManager;

    public Dispatcher(final SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @Override
    public HttpResponse handle(final HttpRequest request) throws IOException {
        try {
            return route(request);
        } catch (IOException | RuntimeException e) {
            log.error("요청을 처리하지 못했습니다.", e);

            return serverError();
        }
    }

    private HttpResponse route(final HttpRequest request) throws IOException {
        final String path = request.requestLine().path();

        if ("/".equals(path) && request.isGet()) {
            byte[] responseBody = "Hello world!".getBytes();

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(CONTENT_TYPE, "text/html;charset=utf-8");
            headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

            return new HttpResponse(
                    new HttpStatusLine(HTTP_VERSION, 200, "OK"),
                    headers,
                    responseBody
            );
        }
        if ("/login".equals(path) && request.isGet()) {
            boolean isLoggedIn = findSession(request)
                    .map(session -> session.getAttribute(USER))
                    .isPresent();

            if (isLoggedIn) {
                return redirectTo(INDEX_PAGE);
            }

            return getStaticResource(LOGIN_PAGE);
        }
        if ("/login".equals(path) && request.isPost()) {
            return login(request);
        }
        if ("/register".equals(path) && request.isGet()) {
            return getStaticResource(REGISTER_PAGE);
        }
        if ("/register".equals(path) && request.isPost()) {
            return register(request);
        }

        return getStaticResource(path);
    }

    private HttpResponse getStaticResource(final String path) throws IOException {
        final URL resource = getClass()
                .getClassLoader().getResource("static" + path);

        if (resource == null) {
            return readResource(requiredResource(NOT_FOUND_PAGE),
                    new HttpStatusLine(HTTP_VERSION, 404, "Not Found"));
        }

        return readResource(resource,
                new HttpStatusLine(HTTP_VERSION, 200, "OK"));
    }

    private URL requiredResource(final String path) {
        return Objects.requireNonNull(
                getClass().getClassLoader().getResource(path),
                path + "이 존재하지 않습니다."
        );
    }

    private HttpResponse readResource(final URL resource, final HttpStatusLine statusLine) throws IOException {
        try (InputStream inputStream = resource.openStream()) {
            byte[] responseBody = inputStream.readAllBytes();

            Map<String, String> headers = new LinkedHashMap<>();
            headers.put(CONTENT_TYPE, contentTypeOf(resource.getPath()));
            headers.put(CONTENT_LENGTH, String.valueOf(responseBody.length));

            return new HttpResponse(statusLine, headers, responseBody);
        }
    }

    private String contentTypeOf(String path) {
        String contentType = URLConnection.guessContentTypeFromName(path);

        if (contentType == null) {
            return "application/octet-stream";
        }

        if (contentType.startsWith("text/")) {
            return contentType + ";charset=utf-8";
        }

        return contentType;
    }

    private HttpResponse login(final HttpRequest request) {
        return findUser(request.body())
                .map(user -> redirectToHomeWithLoggedIn(request, user))
                .orElseGet(() -> redirectTo(UNAUTHORIZED_PAGE));
    }

    private Optional<User> findUser(final Map<String, String> body) {
        final String account = body.get(ACCOUNT);
        final String password = body.get(PASSWORD);

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private HttpResponse register(HttpRequest request) {
        String account = request.body().get(ACCOUNT);
        String email = request.body().get(EMAIL);
        String password = request.body().get(PASSWORD);

        if (account == null || email == null || password == null) {
            log.info("잘못된 회원가입 요청입니다.");

            return redirectTo(UNAUTHORIZED_PAGE);
        }

        if (InMemoryUserRepository.findByAccount(account).isPresent()) {
            log.info("이미 가입된 계정입니다: {}", account);

            return redirectTo(UNAUTHORIZED_PAGE);
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);

        return redirectToHomeWithLoggedIn(request, user);
    }

    private HttpResponse serverError() {
        final HttpStatusLine statusLine =
                new HttpStatusLine(HTTP_VERSION, 500, "Internal Server Error");

        try {
            return readResource(requiredResource(SERVER_ERROR_PAGE), statusLine);
        } catch (IOException | RuntimeException e) {
            log.error("500.html을 읽지 못했습니다.", e);

            return new HttpResponse(statusLine, Map.of(CONTENT_LENGTH, "0"), new byte[0]);
        }
    }

    private HttpResponse redirectTo(final String location) {
        return new HttpResponse(
                new HttpStatusLine(HTTP_VERSION, 302, "Found"),
                Map.of(LOCATION, location),
                new byte[0]
        );
    }

    private HttpResponse redirectToHomeWithLoggedIn(final HttpRequest request, final User user) {
        final Session session = sessionManager.create();
        session.setAttribute(USER, user);

        final Map<String, String> headers = Map.of(
                LOCATION, INDEX_PAGE,
                SET_COOKIE, JSESSIONID + "=" + session.getId()
        );

        return new HttpResponse(
                new HttpStatusLine(HTTP_VERSION, 302, "Found"),
                headers,
                new byte[0]
        );
    }

    private Optional<Session> findSession(final HttpRequest request) {
        return sessionManager.find(request.cookies().get(JSESSIONID));
    }
}
