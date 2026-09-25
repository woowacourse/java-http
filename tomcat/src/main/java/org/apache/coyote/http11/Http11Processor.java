package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.session.Session;
import org.apache.catalina.session.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.stream.Collectors;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String DEFAULT_MESSAGE = "Hello world!";
    private static final String BAD_REQUEST_MESSAGE = "잘못된 요청입니다.";

    private static final String CONTENT_TYPE = "Content-Type";
    private static final String CONTENT_LENGTH = "Content-Length";
    private static final String CHARSET_SUFFIX = ";charset=utf-8";
    private static final String COOKIE = "Cookie";
    private static final String SET_COOKIE = "Set-Cookie";
    private static final String JSESSIONID = "JSESSIONID";
    private static final String LOCATION = "Location";
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String FOUND = "302 FOUND";
    private static final String INDEX_PATH = "/index.html";
    private static final String NOT_FOUND_PATH = "/404.html";
    private static final String UNAUTHORIZED_PATH = "/401.html";
    private static final String REGISTER_PATH = "/register";

    private static final String DEFAULT_CONTENT_TYPE = "text/html";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String JS_EXTENSION = ".js";
    private static final String JS_CONTENT_TYPE = "text/javascript";

    private static final int METHOD_INDEX = 0;
    private static final int PATH_INDEX = 1;
    private static final String STATIC_PREFIX = "static";

    private static final String OK = "200 OK";
    private static final String BAD_REQUEST = "400 Bad Request";
    private static final String NOT_FOUND = "404 Not Found";

    private final Socket connection;

    private static final SessionManager SESSION_MANAGER = SessionManager.getInstance();

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
             final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

            String response;
            try {
                final HttpRequest request = HttpRequest.from(reader);
                if (request == null) {
                    return;
                }
                response = createResponse(request);
            } catch (IllegalArgumentException e) {
                log.warn("잘못된 요청입니다. {}", e.getMessage());
                response = buildResponse(BAD_REQUEST, contentTypeHeader(DEFAULT_CONTENT_TYPE), BAD_REQUEST_MESSAGE);
            }

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final HttpRequest request) throws IOException {
        final String requestPath = request.getPath();
        log.info("{} {} / Cookie: {}", request.getMethod(), requestPath, request.getCookie().get(COOKIE));

        if (request.isPost()) {
            if (LOGIN_PATH.equals(requestPath)) {
                final Optional<User> user = login(request);
                if (user.isPresent()) {
                    log.info("로그인 성공: {}", user.get());
                    final String sessionId = UUID.randomUUID().toString();
                    final Session session = new Session(sessionId);
                    session.setAttribute("user", user.get());
                    SESSION_MANAGER.add(session);

                    final Map<String, String> responseHeaders = new LinkedHashMap<>();
                    responseHeaders.put(LOCATION, INDEX_PATH);
                    responseHeaders.put(SET_COOKIE, JSESSIONID + "=" + sessionId);
                    return buildResponse(FOUND, responseHeaders, "");
                }
                log.info("로그인 실패: {}", request.getParameter("account"));
                return buildRedirect(UNAUTHORIZED_PATH);
            }

            if (REGISTER_PATH.equals(requestPath)) {
                final String account = request.getParameter("account");
                final String password = request.getParameter(("password"));
                final String email = request.getParameter(("email"));
                if (account == null || password == null || email == null) {
                    return buildResponse(BAD_REQUEST, contentTypeHeader(DEFAULT_CONTENT_TYPE), BAD_REQUEST_MESSAGE);
                }
                InMemoryUserRepository.save(new User(account, password, email));
                log.info("회원가입 성공: {}", account);
                return buildRedirect(INDEX_PATH);
            }
        }

        if (LOGIN_PATH.equals(requestPath)) {
            final String sessionId = request.getCookie().getJsessionid();
            if (sessionId != null) {
                final Session session = SESSION_MANAGER.findSession(sessionId);
                if (session != null) {
                    log.info("이미 로그인된 사용자: {}", SESSION_MANAGER.findSession(sessionId).getAttribute("user"));
                    return buildRedirect(INDEX_PATH);
                }
            }
        }

        try {
            final String responseBody = resolveResponseBody(requestPath);
            return buildResponse(OK, contentTypeHeader(resolveContentType(requestPath)), responseBody);
        } catch (RuntimeException e) {
            log.info(e.getMessage());
            return buildResponse(NOT_FOUND, contentTypeHeader(DEFAULT_CONTENT_TYPE), resolveResponseBody(NOT_FOUND_PATH));
        }
    }

    private String buildRedirect(final String location) {
        return buildResponse(FOUND, Map.of(LOCATION, location), "");
    }

    private String buildResponse(final String status, final Map<String, String> headers, final String responseBody) {
        final Map<String, String> responseHeaders = new LinkedHashMap<>(headers);
        responseHeaders.put(CONTENT_LENGTH, String.valueOf(responseBody.getBytes().length));
        final String headerLines = responseHeaders.entrySet().stream()
                .map(header -> header.getKey() + ": " + header.getValue() + " ")
                .collect(Collectors.joining("\r\n"));

        return String.join("\r\n",
                "HTTP/1.1 " + status + " ",
                headerLines,
                "",
                responseBody);
    }

    private Optional<User> login(final HttpRequest request) {
        final String account = request.getParameter("account");
        final String password = request.getParameter("password");
        if (account == null || password == null) {
            return Optional.empty();
        }
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private String resolveContentType(String requestPath) {
        if (requestPath.endsWith(CSS_EXTENSION)) {
            return CSS_CONTENT_TYPE;
        }
        if (requestPath.endsWith(JS_EXTENSION)) {
            return JS_CONTENT_TYPE;
        }
        return DEFAULT_CONTENT_TYPE;
    }

    private String resolveResponseBody(String requestPath) throws IOException {
        if (ROOT_PATH.equals(requestPath)) {
            return DEFAULT_MESSAGE;
        }
        if (!requestPath.contains(".")) {
            requestPath += HTML_EXTENSION;
        }
        return readStaticFile(requestPath);
    }

    private String readStaticFile(final String resourcePath) throws IOException {
        final URL resource = getClass().getClassLoader().getResource(STATIC_PREFIX + resourcePath);
        if (resource == null) {
            throw new RuntimeException("요청한 리소스를 찾을 수 없습니다: " + resourcePath);
        }

        try {
            final Path path = Path.of(resource.toURI());

            if (!Files.isRegularFile(path)) {
                throw new RuntimeException("요청한 리소스를 찾을 수 없습니다: " + resourcePath);
            }
            return Files.readString(path);
        } catch (URISyntaxException e) {
            throw new IOException("잘못된 리소스 경로입니다: " + resourcePath, e);
        }
    }

    private Map<String, String> contentTypeHeader(final String contentType) {
        return Map.of(CONTENT_TYPE, contentType + CHARSET_SUFFIX);
    }
}
