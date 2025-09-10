package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private String responseStatus;
    private Map<String, String> responseHeaders = new LinkedHashMap<>();
    private String responseBody;
    private List<String> responseCookies = new ArrayList<>();

    private static final String STATUS_OK = "HTTP/1.1 200 OK ";
    private static final String STATUS_FOUND = "HTTP/1.1 302 Found ";
    private static final String STATUS_UNAUTHORIZED = "HTTP/1.1 401 Unauthorized ";

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
        ) {
            HttpRequest request = HttpRequestReader.read(inputStream);
            route(request);
            writeResponse(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void route(final HttpRequest request) throws IOException, URISyntaxException {
        if ("GET".equals(request.getMethod())) {
            handleGetMethod(request);
        }
        if("POST".equals(request.getMethod())) {
            handlePostMethod(request);
        }
    }

    private void writeResponse(final OutputStream outputStream) throws IOException {
        String response = buildResponse();
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void handleGetMethod(HttpRequest request) throws IOException, URISyntaxException {
        if ("/".equals(request.getPath())) {
            okResponse("Hello world!", "text/html;charset=utf-8");
            return;
        }
        if ("/login".equals(request.getPath())) {
            if(isLoggedIn(request)) {
                foundResponse("/index.html");
                return;
            }
            okResponse("/login.html");
            return;
        }
        if("/register".equals(request.getPath())) {
            okResponse("/register.html");
            return;
        }
        okResponse(request.getPath());
    }

    private void handlePostMethod(HttpRequest request) throws IOException, URISyntaxException {
        if("/register".equals(request.getPath())) {
            handleRegister(request);
            return;
        }
        if("/login".equals(request.getPath())) {
            handleLogin(request);
            return;
        }
        foundResponse("/404.html");
    }

    private void handleRegister(HttpRequest request) {
        Map<String, String> requestBody = request.getRequestBody();
        User user = new User(requestBody.get("account"), requestBody.get("password"), requestBody.get("email"));
        InMemoryUserRepository.save(user);
        foundResponse("/index.html");
    }

    private void handleLogin(HttpRequest request) throws IOException, URISyntaxException {
        Map<String, String> requestBody = request.getRequestBody();
        if(requestBody.isEmpty()) {
            log.info("login failed: queryString is empty");
            foundResponse("/401.html");
            return;
        }
        if(isLoggedIn(request)) {
            log.info("login successful: already logged in");
            okResponse("/index.html");
            return;
        }
        Optional<User> userOptional = InMemoryUserRepository.findByAccount(requestBody.get("account"));
        if(userOptional.isPresent() && userOptional.get().checkPassword(requestBody.get("password"))) {
            loginInSession(userOptional.get());
            return;
        }
        log.info("login failed: invalid user info");
        foundResponse("/401.html");
    }

    private void loginInSession(final User user) {
        log.info("User{}", user);
        Session session = new Session(UUID.randomUUID().toString());
        SessionManager sessionManager = SessionManager.getInstance();
        sessionManager.add(session);
        session.setAttribute("user", user);
        responseCookies.add("JSESSIONID=" + session.getId());
        foundResponse("/index.html");
        log.info("login successful: logged in");
        return;
    }

    private boolean isLoggedIn(HttpRequest request) {
        HttpCookie httpCookie = request.getHttpCookie();
        if(httpCookie.hasNoSession()) {
            return false;
        }
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(httpCookie.getSessionId());
        if(session == null) {
            return false;
        }
        User user = getUser(session);
        return user != null && InMemoryUserRepository.findByAccount(user.getAccount()).isPresent();
    }

    private User getUser(final Session session) {
        return (User) session.getAttribute("user");
    }

    private void foundResponse(final String redirectLocation) {
        responseStatus = STATUS_FOUND;

        responseHeaders.put("Location", redirectLocation);
        responseHeaders.put("Content-Length", "0");
    }


    private void okResponse(String path) throws IOException, URISyntaxException {
        URL url = getClass().getClassLoader().getResource("static" + path);
        if(url == null) {
            foundResponse("/404.html");
            return;
        }
        responseBody = new String(Files.readAllBytes(Paths.get(url.toURI())));
        responseStatus = STATUS_OK;

        responseHeaders.put("Content-Type", getContentType(path));
        responseHeaders.put("Content-Length", String.valueOf(responseBody.getBytes(StandardCharsets.UTF_8).length));
    }

    private void okResponse(String body, String contentType) {
        responseBody = body;
        responseStatus = STATUS_OK;
        responseHeaders.put("Content-Type", contentType);
        responseHeaders.put("Content-Length", String.valueOf(body.getBytes(StandardCharsets.UTF_8).length));
    }

    private String buildResponse() {
        StringBuilder builder = new StringBuilder();
        builder.append(responseStatus).append("\r\n");

        for (Map.Entry<String, String> entry : responseHeaders.entrySet()) {
            builder.append(entry.getKey()).append(": ").append(entry.getValue()).append(" ").append("\r\n");
        }

        for (String cookie : responseCookies) {
            builder.append("Set-Cookie: ").append(cookie).append(" ").append("\r\n");
        }
        builder.append("\r\n");

        if (responseBody != null) {
            builder.append(responseBody);
        }
        return builder.toString();
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }


}
