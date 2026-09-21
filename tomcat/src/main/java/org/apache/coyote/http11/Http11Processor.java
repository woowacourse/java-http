package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Optional;
import org.apache.catalina.Manager;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCES_PREFIX = "static";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String LOGIN_USER = "loginUser";

    private final Socket connection;
    private final Manager manager;

    public Http11Processor(final Socket connection) {
        this(connection, new SessionManager());
    }

    Http11Processor(final Socket connection, final Manager manager) {
        this.connection = connection;
        this.manager = manager;
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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final HttpRequest request = HttpRequest.from(reader, manager);

            final HttpResponse response = addSessionCookieIfMissing(request, getResponse(request));

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse addSessionCookieIfMissing(final HttpRequest request, final HttpResponse response) {
        if (request.getCookie().get(JSESSIONID).isPresent()) {
            return response;
        }
        final HttpSession session = request.getSession(true);
        return response.addHeader(
                "Set-Cookie",
                Cookie.of(JSESSIONID, session.getId()).toHeaderValue()
        );
    }

    private HttpResponse getResponse(final HttpRequest request) {
        String path = request.getPath();
        if (!path.equals("/") && isResourcePresent(path)) {
            return new HttpResponse(HttpStatus.OK, getContentType(path), modelToView(path));
        }

        if (path.equals("/") && request.getMethod().equals("GET")) {
            return new HttpResponse(HttpStatus.OK, getContentType(path), "Hello world!");
        }
        if (path.equals("/register") && request.getMethod().equals("GET")) {
            String body = modelToView("/register.html");
            return new HttpResponse(HttpStatus.OK, getContentType(path), body);
        }
        if (path.equals("/register") && request.getMethod().equals("POST")) {
            saveUser(request);

            return new HttpResponse(HttpStatus.FOUND, getContentType(path), " ")
                    .addHeader("Location", "/index.html");
        }
        if (path.equals("/login") && request.getMethod().equals("GET")) {
            if (isLoggedIn(request)) {
                return new HttpResponse(HttpStatus.FOUND, getContentType(path), " ")
                        .addHeader("Location", "/index.html");
            }
            return new HttpResponse(HttpStatus.OK, getContentType(path), modelToView("/login.html"));
        }
        if (path.equals("/login") && request.getMethod().equals("POST")) {
            String account = request.getBodyParameter("account");
            String password = request.getBodyParameter("password");

            final Optional<User> loginUser = authenticate(account, password);
            if (loginUser.isPresent()) {
                final HttpSession session = request.getSession(true);
                session.setAttribute(LOGIN_USER, loginUser.get());
                return new HttpResponse(HttpStatus.FOUND, getContentType(path), " ")
                        .addHeader("Location", "/index.html");
            }
            return new HttpResponse(HttpStatus.FOUND, getContentType(path), " ")
                    .addHeader("Location", "/401.html");
        }

        return new HttpResponse(HttpStatus.BAD_REQUEST, getContentType(path), "Bad Request");
    }

    private void saveUser(HttpRequest request) {
        String name = request.getBodyParameter("account");
        String password = request.getBodyParameter("password");
        String email = request.getBodyParameter("email");
        User user = new User(name, password, email);

        InMemoryUserRepository.save(user);
    }

    private Optional<User> authenticate(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final HttpSession session = request.getSession(false);
        return session != null && session.getAttribute(LOGIN_USER) != null;
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private boolean isResourcePresent(String path) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + path);
        return resource != null;
    }

    private String modelToView(String path) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + path);
        if (resource == null) {
            log.info("존재하지 않는 파일 명입니다. 파일 경로를 확인해주세요. path: {}", path);
            return "";
        }
        try {
            URI uri = resource.toURI();
            return Files.readString(Paths.get(uri));
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }
}
