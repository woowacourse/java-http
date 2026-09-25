package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String QUERY_PARAM_DELIMITER = "&";
    private static final String QUERY_PARAM_VALUE_DELIMITER = "=";
    private static final String JSESSION_ID_KEY = "JSESSIONID";
    private static final String LOGIN_USER_KEY = "user";

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
             final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            final HttpRequest httpRequest = new HttpRequest(bufferedReader);

            final HttpResponse response = handleRequest(httpRequest);

            writeResponse(outputStream, response);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(final HttpRequest httpRequest) throws IOException {
        final RequestLine requestLine = httpRequest.getRequestLine();
        final HttpMethod httpMethod = requestLine.getHttpMethod();
        final String target = requestLine.getTarget();
        final URI uri = URI.create(target);
        final String uriPath = uri.getPath();
        final String requestBody = httpRequest.getBody().getContent();

        final HttpHeaders headers = httpRequest.getHeaders();
        final HttpCookie httpCookie = new HttpCookie(headers.getHeader("Cookie"));

        if (uriPath.equals("/login")) {
            return handleLogin(httpMethod, uri, httpCookie, requestBody);
        }

        if (uriPath.equals("/register")) {
            return handleRegister(requestBody);
        }

        return createFileResponse(uriPath, "200 OK");
    }

    private HttpResponse handleLogin(final HttpMethod httpMethod, final URI uri, final HttpCookie httpCookie, final String requestBody) throws IOException {
        final String uriPath = uri.getPath();
        Path filePath = getFilePath(uriPath);

        if (httpMethod == HttpMethod.GET && isLoggedIn(httpCookie)) {
            return new HttpResponse("302 Found", "text/html;charset=utf-8", "", "/index.html", httpCookie);
        }

        final String query = uri.getQuery();
        final String loginParameters = requestBody != null ? requestBody : query;

        if (loginParameters == null) {
            filePath = getFilePath("/login");
            return new HttpResponse("200 OK", getContentType(filePath), getResponseBody(filePath), null, httpCookie);
        }

        final Optional<User> user = authenticate(extractQueryParams(loginParameters));
        if (user.isPresent()) {
            saveUserInSession(httpCookie, user.get());
            return new HttpResponse("302 Found", "text/html;charset=utf-8", "", "/index.html", httpCookie);
        }

        return new HttpResponse("302 Found", "text/html;charset=utf-8", "", "/401.html", httpCookie);
    }

    private boolean isLoggedIn(final HttpCookie httpCookie) throws IOException {
        final String sessionId = httpCookie.get(JSESSION_ID_KEY);
        if (sessionId == null) {
            return false;
        }

        final Session session = SessionManager.getInstance().findSession(sessionId);
        return session != null && session.getAttribute(LOGIN_USER_KEY) != null;
    }

    private void saveUserInSession(final HttpCookie httpCookie, final User user) throws IOException {
        final SessionManager sessionManager = SessionManager.getInstance();
        String sessionId = httpCookie.get(JSESSION_ID_KEY);
        Session session = null;

        if (sessionId != null) {
            session = sessionManager.findSession(sessionId);
        }

        if (session == null) {
            sessionId = UUID.randomUUID().toString();
            session = new Session(sessionId);
            sessionManager.add(session);
            httpCookie.put(JSESSION_ID_KEY, sessionId);
        }

        session.setAttribute(LOGIN_USER_KEY, user);
    }

    private HttpResponse handleRegister(final String requestBody) throws IOException {
        if (requestBody != null) {
            createUser(extractQueryParams(requestBody));
        }

        return createFileResponse("/register", "200 OK");
    }

    private HttpResponse createFileResponse(final String uriPath, final String httpStatus) throws IOException {
        final Path filePath = getFilePath(uriPath);
        final String contentType = getContentType(filePath);
        final String responseBody = getResponseBody(filePath);
        return new HttpResponse(httpStatus, contentType, responseBody, null, null);
    }

    private Path getFilePath(final String uriPath) {
        if (uriPath.equals("/")) {
            return Path.of("/");
        }

        final String resourceName = switch (uriPath) {
            case "/login" -> "static/login.html";
            case "/register" -> "static/register.html";
            default -> "static/" + (uriPath.startsWith("/") ? uriPath.substring(1) : uriPath);
        };

        return resolveResourcePath(resourceName);
    }

    private Map<String, String> extractQueryParams(final String query) {
        final String[] queryParams = query.split(QUERY_PARAM_DELIMITER);

        final Map<String, String> params = new HashMap<>();

        for (String queryParam : queryParams) {
            String[] pair = queryParam.split(QUERY_PARAM_VALUE_DELIMITER, 2);
            String key = pair[0];
            String value = pair.length == 2 ? pair[1] : "";

            params.put(key, value);
        }
        return params;
    }

    private Optional<User> authenticate(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            log.error("login error");
            return Optional.empty();
        }

        log.info("user : {}", user.get());
        return user;
    }

    private void createUser(final Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        User byAccount = InMemoryUserRepository.findByAccount(account)
                .orElseThrow();
        log.info("byAccount = {}", byAccount);
    }

    private String getResponseBody(final Path filePath) throws IOException {
        if (filePath.equals(Path.of("/"))) {
            return "Hello world!";
        }

        return Files.readString(filePath);
    }

    private String getContentType(final Path filePath) {
        final String path = filePath.toString();

        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }

        if (path.endsWith(".js")) {
            return "text/javascript;charset=utf-8";
        }

        return "text/html;charset=utf-8";
    }

    private Path resolveResourcePath(final String name) {
        final URL url = getClass().getClassLoader().getResource(name);
        if (url == null)
            return Path.of("/");
        return Path.of(url.getPath());
    }

    private void writeResponse(final OutputStream outputStream, final HttpResponse response) throws IOException {
        outputStream.write(createResponse(response).getBytes());
        outputStream.flush();
    }

    private String createResponse(final HttpResponse response) {
        log.info("response status: {}", response.status());
        StringBuilder httpResponse = new StringBuilder()
                .append("HTTP/1.1 ").append(response.status()).append(" ").append("\r\n")
                .append("Content-Type: ").append(response.contentType()).append(" ").append("\r\n")
                .append("Content-Length: ").append(response.body().getBytes().length).append(" ").append("\r\n");

        if (response.location() != null) {
            httpResponse.append("Location: ").append(response.location()).append(" ").append("\r\n");
        }
        if (response.cookie() != null && response.cookie().contains(JSESSION_ID_KEY)) {
            httpResponse.append("Set-Cookie: ")
                    .append(JSESSION_ID_KEY)
                    .append("=")
                    .append(response.cookie().get(JSESSION_ID_KEY))
                    .append(" ").append("\r\n");
        }

        return httpResponse.append("\r\n")
                .append(response.body())
                .toString();
    }

    private record HttpResponse(String status, String contentType, String body, String location, HttpCookie cookie) { }
}
