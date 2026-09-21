package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final String SET_COOKIE_HEADER_PREFIX = "Set-Cookie: ";
    private static final String COOKIE_PATH_ATTRIBUTE = "; Path=/";
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String REGISTER_PATH = "/register";
    private static final String USER_SESSION_ATTRIBUTE = "user";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";
    private static final String INDEX_HTML_PATH = "/index.html";
    private static final String UNAUTHORIZED_PAGE_PATH = "/401.html";
    private static final String HTML_EXTENSION = ".html";
    private static final String CSS_EXTENSION = ".css";
    private static final String JS_EXTENSION = ".js";
    private static final String HTML_CONTENT_TYPE = "text/html";
    private static final String CSS_CONTENT_TYPE = "text/css";
    private static final String JS_CONTENT_TYPE = "text/javascript";
    private static final String TEXT_CONTENT_TYPE = "text/plain";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String ACCOUNT_PARAMETER = "account";
    private static final String PASSWORD_PARAMETER = "password";
    private static final String CRLF = "\r\n";
    private static final String OK_STATUS_LINE = "HTTP/1.1 200 OK ";
    private static final String NOT_FOUND_STATUS_LINE = "HTTP/1.1 404 Not Found ";
    private static final String FOUND_STATUS_LINE = "HTTP/1.1 302 FOUND ";
    private static final String CONTENT_TYPE_HEADER_PREFIX = "Content-Type: ";
    private static final String UTF_8_CHARSET_PARAMETER = ";charset=utf-8 ";
    private static final String CONTENT_LENGTH_HEADER_PREFIX = "Content-Length: ";

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(Socket connection, SessionManager sessionManager) {
        this.connection = connection;
        this.sessionManager = sessionManager;
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
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.from(bufferedReader);

            HttpCookie cookie = HttpCookie.create(request.getHeader("Cookie"));
            HttpMethod method = request.getMethod();
            String urlPath = request.getUri().getPath();
            String requestBody = request.getBody();
            boolean shouldSetCookie = !cookie.isSessionId();
            if (shouldSetCookie) {
                cookie.setSessionId();
            }

            Map<String, String> requestBodyMap = new LinkedHashMap<>();
            if (method == HttpMethod.POST && (urlPath.equals(REGISTER_PATH) || urlPath.equals(LOGIN_PATH))
                    && !requestBody.isEmpty()) {
                String[] body = requestBody.split("&");
                for (int i = 0; i < body.length; i++) {
                    String[] value = body[i].split("=");
                    requestBodyMap.put(URLDecoder.decode(value[0], StandardCharsets.UTF_8), URLDecoder.decode(value[1], StandardCharsets.UTF_8));
                }
                if (urlPath.equals(REGISTER_PATH)) {
                    User user = new User(requestBodyMap.get(ACCOUNT_PARAMETER), requestBodyMap.get(PASSWORD_PARAMETER),
                            requestBodyMap.get("email"));
                    InMemoryUserRepository.save(user);
                }
            }

            // 경로 없음 -> Hello world!
            // 경로 존재하면 파일 읽기
            String responseBody;
            boolean isResourceNull = false;
            if (urlPath.equals(ROOT_PATH)) {
                responseBody = DEFAULT_RESPONSE_BODY;
            } else if (urlPath.equals(LOGIN_PATH) || urlPath.equals(REGISTER_PATH)) {
                URL resource = getClass().getClassLoader()
                        .getResource(STATIC_RESOURCE_DIRECTORY + urlPath + HTML_EXTENSION);
                if (resource == null) {
                    isResourceNull = true;
                    responseBody = "요청한 파일을 찾을 수 없습니다.";
                } else {
                    responseBody = Files.readString(
                            Paths.get(resource.toURI()), StandardCharsets.UTF_8);
                }
            } else {
                URL resource = getClass().getClassLoader().getResource(STATIC_RESOURCE_DIRECTORY + urlPath);
                if (resource == null) {
                    isResourceNull = true;
                    responseBody = "요청한 파일을 찾을 수 없습니다.";
                } else {
                    responseBody = Files.readString(Paths.get(resource.toURI()), StandardCharsets.UTF_8);
                }
            }

            // 로그
            boolean isLoginSuccess = false;
            if (urlPath.equals(LOGIN_PATH) && method == HttpMethod.POST && requestBodyMap.containsKey(
                    ACCOUNT_PARAMETER)
                    && requestBodyMap.containsKey(PASSWORD_PARAMETER)) {
                Optional<User> matchedUser = InMemoryUserRepository
                        .findByAccount(requestBodyMap.get(ACCOUNT_PARAMETER))
                        .filter(user -> user.checkPassword(requestBodyMap.get(PASSWORD_PARAMETER)));
                isLoginSuccess = matchedUser.isPresent();

                if (isLoginSuccess) {
                    Session session = sessionManager.getOrCreateSession(cookie.getSessionId());
                    session.addUser(USER_SESSION_ATTRIBUTE, matchedUser.get());
                    log.info("회원 조회 성공: {}", matchedUser);
                }
            }

            // 요청 경로 확장자로 Content-Type 결정
            String contentType = HTML_CONTENT_TYPE;

            if (isResourceNull) {
                contentType = TEXT_CONTENT_TYPE;
            } else if (urlPath.endsWith(CSS_EXTENSION)) {
                contentType = CSS_CONTENT_TYPE;
            } else if (urlPath.endsWith(JS_EXTENSION)) {
                contentType = JS_CONTENT_TYPE;
            }

            List<String> responseHeaders = new ArrayList<>();
            if (isResourceNull) {
                responseHeaders.add(NOT_FOUND_STATUS_LINE);
            } else if (urlPath.equals(LOGIN_PATH) && method == HttpMethod.POST) {
                String location;
                if (isLoginSuccess) {
                    location = INDEX_HTML_PATH;
                } else {
                    location = UNAUTHORIZED_PAGE_PATH;
                }
                responseHeaders.add(FOUND_STATUS_LINE);
                responseHeaders.add("Location: " + location);
                responseBody = "";
            } else if (method == HttpMethod.POST && urlPath.equals(REGISTER_PATH)) {
                responseHeaders.add(FOUND_STATUS_LINE);
                responseHeaders.add("Location: " + INDEX_HTML_PATH);
                responseBody = "";
            } else if (method == HttpMethod.GET && urlPath.equals(LOGIN_PATH) && sessionManager.isSessionContainsKey(cookie.getSessionId(), USER_SESSION_ATTRIBUTE)) {
                responseHeaders.add(FOUND_STATUS_LINE);
                responseHeaders.add("Location: " + INDEX_HTML_PATH);
                responseBody = "";
            }else {
                responseHeaders.add(OK_STATUS_LINE);
            }

            responseHeaders.add(CONTENT_TYPE_HEADER_PREFIX + contentType + UTF_8_CHARSET_PARAMETER);
            responseHeaders.add(CONTENT_LENGTH_HEADER_PREFIX
                    + responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
            if (shouldSetCookie) {
                responseHeaders.add(SET_COOKIE_HEADER_PREFIX + cookie.getSessionIdCookieName() + "="
                        + cookie.getSessionId() + COOKIE_PATH_ATTRIBUTE);
            }

            final String response = String.join(CRLF, responseHeaders) + CRLF + CRLF + responseBody;

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }
}
