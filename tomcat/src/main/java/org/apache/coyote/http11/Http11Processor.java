package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final SessionManager sessionManager = SessionManager.getInstance();

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
    public void process(Socket connection) {
        try (InputStream inputStream = connection.getInputStream();
             OutputStream outputStream = connection.getOutputStream()) {
            HttpRequest request = HttpRequestParser.parse(inputStream);
            HttpResponse response = handleRequest(request);
            outputStream.write(response.build().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException exception) {
            log.error(exception.getMessage(), exception);
        }
    }

    private HttpResponse handleRequest(HttpRequest request) {
        if (request.isGetMethod()) {
            return handleGetRequest(request);
        } else if (request.isPostMethod()) {
            return handlePostRequest(request);
        }
        throw new IllegalArgumentException("처리할 수 없는 요청입니다.");
    }

    private HttpResponse handleGetRequest(HttpRequest request) {
        if (hasPath(request)) {
            StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.OK);
            String body = buildResponseBody(request.getPathWithoutQueryString());
            HttpHeaders headers = new HttpHeaders();
            headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue(request.getPathWithoutQueryString()));
            headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
            return new HttpResponse(statusLine, headers, body);
        }

        if (request.getPath().equals("/")) {
            StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.OK);
            String body = buildResponseBody("/index.html");
            HttpHeaders headers = new HttpHeaders();
            headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue("/index.html"));
            headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
            return new HttpResponse(statusLine, headers, body);
        }
        if (request.getPath().equals("/register")) {
            StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.OK);
            String body = buildResponseBody("/register.html");
            HttpHeaders headers = new HttpHeaders();
            headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue("/register.html"));
            headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
            return new HttpResponse(statusLine, headers, body);
        }
        if (request.getPathWithoutQueryString().equals("/login")) {
            parseQueryString(request);

            if (isLoggedIn(request)) {
                StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.FOUND);
                String body = buildResponseBody("/index.html");
                HttpHeaders headers = new HttpHeaders();
                headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue("/index.html"));
                headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
                return new HttpResponse(statusLine, headers, body);
            }

            StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.OK);
            String body = buildResponseBody("/login.html");
            HttpHeaders headers = new HttpHeaders();
            headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue("/login.html"));
            headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
            return new HttpResponse(statusLine, headers, body);
        }

        throw new IllegalArgumentException("처리할 수 없는 GET 요청입니다.");
    }

    private boolean hasPath(HttpRequest request) {
        try {
            buildPath(request.getPath());
            return true;
        } catch (IllegalArgumentException exception) {
            return false;
        }
    }

    private String buildResponseBody(String path) {
        try (BufferedReader bufferedReader = Files.newBufferedReader(buildPath(path))) {
            return bufferedReader.lines()
                    .collect(Collectors.joining("\n")) + "\n";
        } catch (IOException e) {
            return "";
        }
    }

    private String buildContentTypeValue(String path) {
        try {
            return Files.probeContentType(buildPath(path)) + ";charset=utf-8;";
        } catch (IOException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private Path buildPath(String path) {
        URL resource = getClass().getResource("/static" + path);
        try {
            return Path.of(resource.toURI());
        } catch (URISyntaxException exception) {
            throw new IllegalArgumentException(exception);
        }
    }

    private String buildContentLengthValue(String body) {
        return String.valueOf(body.getBytes().length);
    }

    private void parseQueryString(HttpRequest request) {
        Map<String, String> queryString = request.getQueryString();
        Optional<User> user = InMemoryUserRepository.findByAccount(queryString.get("account"));
        if (user.isPresent() && user.get().checkPassword(queryString.get("password"))) {
            log.info("user : {}", user.get());
        }
    }

    private boolean isLoggedIn(HttpRequest request) {
        Optional<String> jsessionid = request.findCookieByName("JSESSIONID");
        if (jsessionid.isEmpty()) {
            return false;
        }
        Session session = sessionManager.findSession(jsessionid.get());
        log.info("user : {}", session.getAttribute("user"));
        return true;
    }

    private HttpResponse handlePostRequest(HttpRequest request) {
        if (request.getPath().equals("/register")) {
            registerUser(request);

            StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.FOUND);
            String body = buildResponseBody("/index.html");
            HttpHeaders headers = new HttpHeaders();
            headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue("/index.html"));
            headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
            headers.add(HeaderType.LOCATION, buildLocationValue("/index.html"));
            return new HttpResponse(statusLine, headers, body);
        }
        if (request.getPath().equals("/login")) {
            Map<String, String> requestBody = request.getBody();
            Optional<User> user = InMemoryUserRepository.findByAccount(requestBody.get("account"));

            if (user.isPresent() && user.get().checkPassword(requestBody.get("password"))) {
                StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.FOUND);
                String body = buildResponseBody("/index.html");
                HttpHeaders headers = new HttpHeaders();
                headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue("/index.html"));
                headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
                headers.add(HeaderType.LOCATION, buildLocationValue("/index.html"));
                headers.add(HeaderType.SET_COOKIE, buildSetCookieValue(user.get()));
                return new HttpResponse(statusLine, headers, body);
            }
            StatusLine statusLine = new StatusLine(request.getProtocolVersion(), Status.FOUND);
            String body = buildResponseBody("/401.html");
            HttpHeaders headers = new HttpHeaders();
            headers.add(HeaderType.CONTENT_TYPE, buildContentTypeValue("/401.html"));
            headers.add(HeaderType.CONTENT_LENGTH, buildContentLengthValue(body));
            headers.add(HeaderType.LOCATION, buildLocationValue("/401.html"));
            return new HttpResponse(statusLine, headers, body);
        }

        throw new IllegalArgumentException("처리할 수 없는 POST 요청입니다.");
    }

    private void registerUser(HttpRequest request) {
        Map<String, String> requestBody = request.getBody();
        String account = requestBody.get("account");
        String password = requestBody.get("password");
        String email = requestBody.get("email");
        InMemoryUserRepository.save(new User(account, password, email));
    }

    private String buildLocationValue(String path) {
        return "http://localhost:8080" + path;
    }

    private String buildSetCookieValue(User user) {
        Session session = Session.create();
        session.setAttribute("user", user);
        sessionManager.add(session);
        return "JSESSIONID=" + session.getId();
    }
}
