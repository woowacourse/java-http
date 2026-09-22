package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.HttpRequestReader;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpResponseWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String STATIC_RESOURCE_ROOT = "static/";
    private static final String CONTENT_TYPE_HTML = "text/html;charset=utf-8";
    private static final String CONTENT_TYPE_CSS = "text/css;charset=utf-8";
    private static final String CONTENT_TYPE_JS = "text/javascript;charset=utf-8";

    private static final String INDEX_PATH = "/index.html";
    private static final String CSS_PATH = "/css/styles.css";
    private static final String JOIN_PATH = "/register";
    private static final String LOGIN_PATH = "/login";
    private static final String UNAUTHORIZED_PATH = "/401.html";

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

            HttpResponseWriter responseWriter = new HttpResponseWriter();
            HttpRequestReader requestReader = new HttpRequestReader();
            HttpRequest request = requestReader.read(inputStream);

            if (request == null) {
                return;
            }

            String method = request.getMethod();
            String path = request.getPath();

            String cookieHeader = request.getHeaders().get("Cookie");

            if (cookieHeader == null) {
                cookieHeader = "";
            }

            Map<String, Cookie> cookies = parseCookies(cookieHeader);
            Cookie sessionCookie = cookies.get("JSESSIONID");
            Map<String, String> responseHeaders = new HashMap<>();

            SessionManager sessionManager = SessionManager.getInstance();
            Session session = null;

            if (sessionCookie != null) {
                session = sessionManager.findSession(sessionCookie.getValue());
            }

            if (session == null) {
                String sessionId = UUID.randomUUID().toString();

                session = new Session(sessionId);
                sessionManager.add(session);

                sessionCookie = new Cookie("JSESSIONID", sessionId);

                responseHeaders.put(
                        "Set-Cookie",
                        sessionCookie.getName() + "=" + sessionCookie.getValue() + "; Path=/"
                );
            }

            var responseBody = "Hello world!";
            var contentType = CONTENT_TYPE_HTML;

            if (path.equals(INDEX_PATH)) {
                responseBody = readStaticFile(path.substring(1));
            }

            if (path.equals(CSS_PATH)) {
                responseBody = readStaticFile(path.substring(1));
                contentType = CONTENT_TYPE_CSS;
            }

            if (path.equals("/js/scripts.js")
                    || path.equals("/assets/chart-area.js")
                    || path.equals("/assets/chart-bar.js")
                    || path.equals("/assets/chart-pie.js")) {

                responseBody = readStaticFile(path.substring(1));
                contentType = CONTENT_TYPE_JS;
            }

            if (method.equals("GET") && path.equals(UNAUTHORIZED_PATH)) {
                responseBody = readStaticFile(UNAUTHORIZED_PATH.substring(1));
            }

            if (method.equals("GET") && path.equals(JOIN_PATH)) {
                responseBody = readStaticFile("register.html");
            }

            if (method.equals("POST") && path.equals(JOIN_PATH)) {
                Map<String, String> parameters = request.getBody().parseFormData();

                String account = parameters.getOrDefault("account", "");
                String email = parameters.getOrDefault("email", "");
                String password = parameters.getOrDefault("password", "");

                User newUser = new User(account, password, email);
                InMemoryUserRepository.save(newUser);

                HttpResponse response = createRedirectResponse(INDEX_PATH, responseHeaders);

                responseWriter.write(outputStream, response);
                return;
            }

            if (method.equals("GET") && path.equals(LOGIN_PATH)) {
                User loginUser = (User) session.getAttribute("user");

                if (loginUser != null) {
                    HttpResponse response = createRedirectResponse(INDEX_PATH, responseHeaders);

                    responseWriter.write(outputStream, response);
                    return;
                }

                responseBody = readStaticFile("login.html");
            }

            if (method.equals("POST") && path.equals(LOGIN_PATH)) {
                Map<String, String> parameters = request.getBody().parseFormData();

                String account = parameters.getOrDefault("account", "");
                String password = parameters.getOrDefault("password", "");

                var user = InMemoryUserRepository.findByAccount(account);

                String location = UNAUTHORIZED_PATH;

                if (user.isPresent() && user.get().checkPassword(password)) {
                    session.setAttribute("user", user.get());

                    log.info("로그인 성공 : account={}", user.get().getAccount());

                    location = INDEX_PATH;
                }

                HttpResponse response = createRedirectResponse(location, responseHeaders);

                responseWriter.write(outputStream, response);
                return;
            }

            HttpResponse response = createOkResponse(contentType, responseBody, responseHeaders);

            responseWriter.write(outputStream, response);
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createOkResponse(
            String contentType,
            String responseBody,
            Map<String, String> responseHeaders
    ) {
        Map<String, String> headers = new HashMap<>(responseHeaders);
        headers.put("Content-Type", contentType);

        return new HttpResponse(
                "HTTP/1.1",
                200,
                "OK",
                new HttpHeaders(headers),
                responseBody.getBytes(StandardCharsets.UTF_8)
        );
    }

    private HttpResponse createRedirectResponse(
            String location,
            Map<String, String> responseHeaders
    ) {
        Map<String, String> headers = new HashMap<>(responseHeaders);
        headers.put("Location", location);

        return new HttpResponse(
                "HTTP/1.1",
                302,
                "Found",
                new HttpHeaders(headers),
                new byte[0]
        );
    }

    // 입력 예시: "yummy_cookie=choco; JSESSIONID=abc123"
    private Map<String,Cookie> parseCookies(String cookieHeader) {
        Map<String, Cookie> cookies = new HashMap<>();

        for (String part : cookieHeader.split(";")) {
            String[] nameValue = part.trim().split("=", 2);

            if (nameValue.length != 2) {
                continue;
            }

            String name = nameValue[0].trim();
            String value = nameValue[1].trim();

            cookies.put(name, new Cookie(name, value));
        }

        return cookies;
    }

    private String readStaticFile(String fileName) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader()
                .getResource(STATIC_RESOURCE_ROOT + fileName);

        Path filePath = Path.of(resource.toURI());
        return Files.readString(filePath, StandardCharsets.UTF_8);
    }
}
