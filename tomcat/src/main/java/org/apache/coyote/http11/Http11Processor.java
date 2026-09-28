package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.apache.coyote.login.LoginParser;
import org.apache.coyote.session.Session;
import org.apache.coyote.session.SessionManager;
import org.apache.coyote.http11.request.Cookie;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private HttpRequest request;

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
            request = HttpRequest.from(inputStream);
            final var resourceType = resolveResourceType(request.header("Accept").orElse("*/*"));
            processRequest(outputStream, resourceType);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processRequest(OutputStream output, String resourceType) throws IOException {
        if (processLogin(output, resourceType) || processRegistration(output, resourceType)) {
            return;
        }
        writeResponse(output, buildDefaultResponse(resourceType));
    }

    private boolean processLogin(OutputStream output, String responseType) throws IOException {
        if (!isLoginRequest()) {
            return false;
        }
        if (request.requestLine().method() == HttpMethod.GET) {
            return redirectLoggedInUser(output, responseType);
        }
        if (request.requestLine().method() != HttpMethod.POST) {
            return false;
        }
        return authenticateLogin(output, responseType);
    }

    private boolean isLoginRequest() {
        return request.requestLine().path().resource().equals("/login");
    }

    private boolean redirectLoggedInUser(OutputStream output, String responseType) throws IOException {
        if (findLoginUser().isEmpty()) {
            return false;
        }
        String body = loadStaticResource("/index", responseType);
        writeResponse(output, HttpResponse.redirect("/index.html", body, responseType));
        return true;
    }

    private boolean authenticateLogin(OutputStream output, String responseType) throws IOException {
        Map<String, String> body = parseRequestBody();
        Optional<User> user = authenticateUser(body.get("account"), body.get("password"));
        if (user.isEmpty()) {
            writeResponse(output, buildUnauthorizedRedirect(responseType));
            return true;
        }
        writeResponse(output, buildLoginSuccessResponse(user.get(), responseType));
        return true;
    }

    private boolean processRegistration(OutputStream output, String responseType) throws IOException {
        if (!isRegistrationRequest()) {
            return false;
        }
        Map<String, String> body = parseRequestBody();
        boolean success = registerUser(body.get("account"), body.get("password"), body.get("email"));
        writeResponse(output, success
            ? HttpResponse.redirect("/index.html", loadStaticResource("/index", responseType), responseType)
            : buildUnauthorizedRedirect(responseType));
        return true;
    }

    private boolean isRegistrationRequest() {
        return request.requestLine().method() == HttpMethod.POST
            && request.requestLine().path().resource().equals("/register");
    }

    private void writeResponse(OutputStream output, HttpResponse response) throws IOException {
        output.write(response.toBytes());
        output.flush();
    }

    private String resolveResourceType(String accept) {
        if (accept.contains("text/css") || accept.contains("css")) {
            return "css";
        }
        if (accept.contains("text/html") || accept.contains("*/*")) {
            return "html";
        }
        return "";
    }

    private String loadStaticResource(String resourceName, String resourceType) {
        final String filePath = resolveResourceFilePath(resourceName, resourceType);
        final URL resource = getClass().getResource("/static" + filePath);
        if (resource == null) {
            return missingResource(filePath);
        }
        return readResource(resource, filePath);
    }

    private String missingResource(String fileName) {
        log.error("[getStaticResource] 파일을 찾을 수 없습니다. path = {}", "/static" + fileName);
        return "Hello world!";
    }

    private String readResource(URL resource, String fileName) {
        try {
            return Files.readString(Path.of(resource.toURI()));
        } catch (IOException | URISyntaxException e) {
            return missingResource(fileName);
        }
    }

    private String resolveResourceFilePath(String resourceName, String resourceType) {
        String resourcePath = request.requestLine().path().resource();
        final String extension = "." + resourceType;
        if (resourceName != null) {
            return resourceName + extension;
        }
        return appendExtension(resourcePath, extension);
    }

    private String appendExtension(String resourcePath, String extension) {
        if (resourcePath.endsWith(extension)) {
            return resourcePath;
        }
        return resourcePath + extension;
    }

    private Map<String, String> parseFormUrlEncodedBody() {
        final String body = request.body();
        if (body == null || body.isBlank()) {
            return new HashMap<>();
        }

        return LoginParser.parseQueryString(body);
    }

    private Map<String, String> parseRequestBody() {
        boolean isFormUrlEncoded = request.header("Content-Type")
            .map(value -> value.startsWith("application/x-www-form-urlencoded"))
            .orElse(false);
        if (isFormUrlEncoded) {
            return parseFormUrlEncodedBody();
        }

        log.debug("[parseRequestBody] 지원하지 않는 Content-Type 입니다.");
        return new HashMap<>();
    }

    private Optional<User> authenticateUser(String account, String password) {
        try {
            User user = InMemoryUserRepository.findByAccount(account).orElseThrow();
            return authenticatedUser(user, password);
        } catch (Exception e) {
            log.error("[authenticateUser] 회원 정보를 찾을 수 없습니다.");
            return Optional.empty();
        }
    }

    private Optional<User> authenticatedUser(User user, String password) {
        if (!user.checkPassword(password)) {
            log.info("[authenticateUser] 회원 정보가 일치하지 않습니다.");
            return Optional.empty();
        }
        log.info("user : {}", user);
        return Optional.of(user);
    }

    private Session createSession(User user) {
        final var session = new Session(UUID.randomUUID().toString());
        session.setAttribute("user", user);
        SessionManager.add(session);
        return session;
    }

    private Optional<User> findLoginUser() {
        Cookie cookies = Cookie.from(request.header("Cookie").orElse(""));
        final var sessionId = cookies.get("JSESSIONID");
        if (sessionId == null) {
            return Optional.empty();
        }
        return findSessionUser(sessionId);
    }

    private Optional<User> findSessionUser(String sessionId) {
        final var session = SessionManager.findSession(sessionId);
        if (session == null) {
            return Optional.empty();
        }
        final var user = session.getAttribute("user");
        return user instanceof User loginUser ? Optional.of(loginUser) : Optional.empty();
    }

    private boolean registerUser(String account, String password, String email) {
        try {
            if (account == null || password == null || email == null) {
                return false;
            }

            InMemoryUserRepository.save(new User(account, password, email));
            return true;
        } catch (Exception e) {
            log.error("[registerUser] 알 수 없는 에러가 발생했습니다.");
            return false;
        }
    }

    private HttpResponse buildDefaultResponse(String responseType) {
        String body = loadStaticResource(null, responseType);
        return HttpResponse.ok(body, responseType);
    }

    private HttpResponse buildUnauthorizedRedirect(String responseType) {
        String body = loadStaticResource("/401", responseType);
        return HttpResponse.redirect("/401", body, responseType);
    }

    private HttpResponse buildLoginSuccessResponse(User user, String responseType) {
        String body = loadStaticResource("/index", responseType);
        Session session = createSession(user);
        return HttpResponse.redirectWithCookie("/index.html", body, responseType, session.getId());
    }
}
