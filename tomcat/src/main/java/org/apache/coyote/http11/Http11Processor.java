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
    private static final String GET = "GET";
    private static final String POST = "POST";
    private static final String ROOT_PATH = "/";
    private static final String REGISTER_PATH = "/register";
    private static final String LOGIN_PATH = "/login";

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

            final HttpResponse response = dispatch(reader);

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse dispatch(BufferedReader reader) throws IOException {
        try {
            final HttpRequest request = HttpRequest.from(reader, manager);
            return addSessionCookieIfMissing(request, getResponse(request));
        } catch (InvalidHttpRequestException e) {
            return HttpResponse.badRequest(e.getMessage());
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
        final String path = request.getPath();
        if (!path.equals(ROOT_PATH) && isResourcePresent(path)) {
            return render(path);
        }

        if (request.matches(GET, ROOT_PATH)) {
            return HttpResponse.ok(getContentType(path), "Hello world!");
        }
        if (request.matches(GET, REGISTER_PATH)) {
            return render("/register.html");
        }
        if (request.matches(POST, REGISTER_PATH)) {
            return register(request);
        }
        if (request.matches(GET, LOGIN_PATH)) {
            return showLoginPage(request);
        }
        if (request.matches(POST, LOGIN_PATH)) {
            return login(request);
        }

        return new HttpResponse(HttpStatus.NOT_FOUND, getContentType(path), "해당하는 경로가 없습니다.");
    }

    private HttpResponse render(final String path) {
        return HttpResponse.ok(getContentType(path), modelToView(path));
    }

    private HttpResponse register(final HttpRequest request) {
        saveUser(request);
        return HttpResponse.redirect("/index.html");
    }

    private HttpResponse showLoginPage(final HttpRequest request) {
        if (isLoggedIn(request)) {
            return HttpResponse.redirect("/index.html");
        }
        return render("/login.html");
    }

    private HttpResponse login(final HttpRequest request) {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");

        final Optional<User> loginUser = authenticate(account, password);
        if (loginUser.isEmpty()) {
            return HttpResponse.redirect("/401.html");
        }

        final HttpSession session = request.getSession(true);
        session.setAttribute(LOGIN_USER, loginUser.get());
        return HttpResponse.redirect("/index.html");
    }

    private void saveUser(final HttpRequest request) {
        final String account = request.getBodyParameter("account");
        final String password = request.getBodyParameter("password");
        final String email = request.getBodyParameter("email");
        final User user = new User(account, password, email);

        InMemoryUserRepository.save(user);
    }

    private Optional<User> authenticate(final String account, final String password) {
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
