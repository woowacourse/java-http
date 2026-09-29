package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Optional;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String SESSION_COOKIE_NAME = "JSESSIONID";
    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

    private static final Map<String, String> RESOURCE_PATH_BY_REQUEST_PATH = Map.of(
            LOGIN_PATH,  "/login.html",
            REGISTER_PATH, "/register.html"
    );

    private static final Map<String, String> CONTENT_TYPE_BY_EXTENSION = Map.of(
            ".html", "text/html;charset=utf-8",
            ".css", "text/css;charset=utf-8",
            ".js", "application/javascript;charset=utf-8"
    );

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
             final var outputStream = connection.getOutputStream()) {

            final HttpResponse response = new HttpResponse(outputStream);

            final HttpRequest request;
            try {
                request = HttpRequest.read(inputStream);
            } catch (IllegalArgumentException e) {
                writeBadRequest(response);
                return;
            }

            if (request == null) {
                return;
            }

            final String method = request.getMethod();
            final String path = request.getPath();
            final HttpCookie cookies = new HttpCookie(request.getHeader("Cookie"));
            final String sessionId = cookies.get(SESSION_COOKIE_NAME);
            Session session = sessionId == null ? null : SESSION_MANAGER.findSession(sessionId);

            if (sessionId == null || sessionId.isBlank()) {
                session = SESSION_MANAGER.createSession();
                response.addHeader("Set-Cookie", SESSION_COOKIE_NAME + "=" + session.getId());
            }

            if ("GET".equals(method)
                    && LOGIN_PATH.equals(path)
                    && session != null
                    && session.getAttribute("user") != null) {
                response.setStatus(302, "Found");
                response.addHeader("Location", "/index.html");
                response.write();
                return;
            }

            if ("POST".equals(method) && (REGISTER_PATH.equals(path) || LOGIN_PATH.equals(path))) {
                final Map<String, String> parameters;
                try {
                    parameters = request.getFormParameters();
                } catch (IllegalArgumentException e) {
                    writeBadRequest(response);
                    return;
                }

                final String location;
                if (REGISTER_PATH.equals(path)) {
                    location = register(parameters) ? "/index.html" : REGISTER_PATH;
                } else {
                    final Optional<User> user = login(parameters);

                    if (user.isPresent()) {
                        if (session == null) {
                            session = SESSION_MANAGER.createSession();
                            response.addHeader("Set-Cookie", SESSION_COOKIE_NAME + "=" + session.getId());
                        }
                        session.setAttribute("user", user.get());
                        location = "/index.html";
                    } else {
                        location = "/401.html";
                    }
                }

                response.setStatus(302, "Found");
                response.addHeader("Location", location);
                response.write();
                return;
            }

            final ResponseData responseData = loadResponseData(path);
            response.addHeader("Content-Type", responseData.contentType());
            response.setBody(responseData.body());
            response.write();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void writeBadRequest(final HttpResponse response) throws IOException {
        response.setStatus(400, "Bad Request");
        response.write();
    }

    private boolean register(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        final String email = parameters.get("email");

        if (account == null || account.isBlank() || password == null || password.isBlank() || email == null || email.isBlank()) {
            return false;
        }

        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return true;
    }

    private Optional<User> login(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(found -> found.checkPassword(password));

        user.ifPresent(found -> log.info("로그인한 회원: {}", account));
        return user;
    }

    private ResponseData loadResponseData(final String requestPath) throws IOException {
        if (ROOT_PATH.equals(requestPath)) {
            return new ResponseData(
                    DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8),
                    DEFAULT_CONTENT_TYPE
            );
        }

        final String resourcePath = STATIC_RESOURCE_DIRECTORY + RESOURCE_PATH_BY_REQUEST_PATH.getOrDefault(requestPath, requestPath);

        final InputStream resource = getClass().
                getClassLoader().
                getResourceAsStream(resourcePath);

        if (resource == null) {
            return new ResponseData(
                    DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8),
                    DEFAULT_CONTENT_TYPE
            );
        }

        try (resource) {
            return new ResponseData(resource.readAllBytes(), contentTypeOf(resourcePath));
        }
    }

    private String contentTypeOf(final String resourcePath) {
        return CONTENT_TYPE_BY_EXTENSION.entrySet().stream()
                .filter(entry -> resourcePath.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT_CONTENT_TYPE);
    }

    private record ResponseData(byte[] body, String contentType) {
    }
}
