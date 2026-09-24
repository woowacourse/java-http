package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.ServletContext;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpSessionContext;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.apache.catalina.Manager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    public static final String FAVICON_PATH = "/favicon.ico";
    public static final String STATIC_PATH = "static";
    public static final String QUERY_DELIMITER = "?";
    public static final String PARAM_DELIMITER = "&";
    public static final String PARAM_EQUAL = "=";

    public static final String SLASH = "/";
    public static final String EXTENSION_DELIMITER = ".";
    public static final String HTML_EXTENSION = ".html";
    public static final String CSS_EXTENSION = ".css";
    public static final String JS_EXTENSION = ".js";

    public static final String CSS_CONTENT_TYPE = "text/css";
    public static final String JS_CONTENT_TYPE = "text/javascript";
    public static final String HTML_CONTENT_TYPE = "text/html";

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.info("connect host: {}, port: {}", connection.getInetAddress(), connection.getPort());
        try {
            process(connection);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void process(final Socket connection) throws URISyntaxException {
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream();
             BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            String[] requestLine = bufferedReader.readLine().split(" ");
            String method = requestLine[0];
            String uri = requestLine[1];
            if (uri.equals(FAVICON_PATH)) {
                return;
            }

            int index = uri.indexOf(QUERY_DELIMITER);
            String path = findPath(uri, index);

            String line;
            int contentLength = 0;
            String cookieHeader = "";
            while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.split(":", 2)[1].trim());
                }
                if (line.startsWith("Cookie:")) {
                    cookieHeader = line.split(":", 2)[1].trim();
                }
            }

            HttpCookie cookie = new HttpCookie(cookieHeader);
            String setCookieHeader = "";
            if (cookie.get("JSESSIONID") == null) {
                setCookieHeader = "Set-Cookie: JSESSIONID=" + UUID.randomUUID() + "\r\n";
            }

            SessionManager sessionManager = SessionManager.getInstance();
            HttpSession session = sessionManager.findSession(cookie.get("JSESSIONID"));

            HttpStatus httpStatus = HttpStatus.OK;
            if (method.equals("GET") && path.equals("static/login.html")
                    && session != null && session.getAttribute("user") != null) {
                httpStatus = HttpStatus.FOUND;
            }

            if (method.equals("POST")) {
                String requestBody = readRequestBody(bufferedReader, contentLength);
                Map<String, String> params = parseParams(requestBody);

                if (path.equals("static/register.html")) {
                    String account = params.get("account");
                    if (InMemoryUserRepository.findByAccount(account).isPresent()) {
                        httpStatus = HttpStatus.CONFLICT;
                    } else {
                        User user = new User(account, params.get("password"), params.get("email"));
                        InMemoryUserRepository.save(user);
                        httpStatus = HttpStatus.FOUND;
                    }
                }

                if (path.equals("static/login.html")) {
                    Session loginSession = new Session(UUID.randomUUID().toString());
                    httpStatus = findUser(params, loginSession);
                    if (httpStatus == HttpStatus.FOUND) {
                        sessionManager.add(loginSession);
                        setCookieHeader = "Set-Cookie: JSESSIONID=" + loginSession.getId() + "\r\n";
                    }
                }
            }

            if (httpStatus == HttpStatus.FOUND || httpStatus == HttpStatus.UNAUTHORIZED) {
                String location = httpStatus == HttpStatus.FOUND ? "/index.html" : "/401.html";
                final String response = String.join("\r\n",
                        "HTTP/1.1 " + HttpStatus.FOUND.getHttpStatus() + " ",
                        setCookieHeader + "Location: " + location + " ",
                        "Content-Length: 0 ",
                        "",
                        "");
                outputStream.write(response.getBytes());
                outputStream.flush();

                return;
            }

            final Path filePath = getPath(path);
            final String responseBody = httpStatus == HttpStatus.CONFLICT
                    ? "이미 존재하는 아이디입니다."
                    : findResponseBody(filePath);

            final String response = String.join("\r\n",
                    "HTTP/1.1 " + httpStatus.getHttpStatus() + " ",
                    setCookieHeader + "Content-Type: " + findContentType(filePath) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestBody(BufferedReader bufferedReader, int contentLength) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        for (int i = 0; i < contentLength; i++) {
            int character = bufferedReader.read();

            if (character == -1) {
                break;
            }

            requestBody.append((char) character);
        }

        return requestBody.toString();
    }

    private String findPath(String uri, int index) {
        String path = uri;
        if (index != -1) {
            path = uri.substring(0, index);
        }

        if (!path.isBlank()) {
            if (!path.equals(SLASH) && !path.contains(EXTENSION_DELIMITER)) {
                path += HTML_EXTENSION;
            }
        }

        return STATIC_PATH + path;
    }

    private Map<String, String> parseParams(String params) {
        Map<String, String> queryParams = new HashMap<>();

        if (params.isBlank()) {
            return queryParams;
        }

        for (String query : params.split(PARAM_DELIMITER)) {
            String[] q = query.split(PARAM_EQUAL, 2);
            if (q.length != 2) {
                continue;
            }

            String key = URLDecoder.decode(q[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(q[1], StandardCharsets.UTF_8);

            queryParams.put(key, value);
        }

        return queryParams;
    }

    private Path getPath(String path) throws URISyntaxException {
        URL resource = getClass().getClassLoader().getResource(path);
        return Paths.get(Objects.requireNonNull(resource).toURI());
    }

    private String findContentType(Path filePath) {
        if (filePath.toString().endsWith(CSS_EXTENSION)) {
            return CSS_CONTENT_TYPE;
        } else if (filePath.toString().endsWith(JS_EXTENSION)) {
            return JS_CONTENT_TYPE;
        }
        return HTML_CONTENT_TYPE;
    }

    private String findResponseBody(Path filePath) throws IOException {
        if (Files.isDirectory(filePath)) {
            return "Hello world!";
        }

        return Files.readString(filePath);
    }

    private HttpStatus findUser(Map<String, String> queryParams, Session session) {
        if (queryParams.isEmpty()) {
            return HttpStatus.OK;
        }

        final String account = queryParams.get("account");
        if (account == null || account.isBlank()) {
            log.info("아이디는 필수값입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        final String password = queryParams.get("password");
        if (password == null || password.isBlank()) {
            log.info("비밀번호는 필수값입니다.");
            return HttpStatus.UNAUTHORIZED;
        }

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return HttpStatus.UNAUTHORIZED;
        }

        User foundUser = user.get();
        if (checkPassword(queryParams, foundUser)) {
            session.setAttribute("user", foundUser);
            log.info("user: {}", foundUser);
            return HttpStatus.FOUND;
        }

        return HttpStatus.UNAUTHORIZED;
    }

    private boolean checkPassword(Map<String, String> queryParams, User user) {
        if (!user.checkPassword(queryParams.get("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return false;
        }

        return true;
    }

    public static class Session implements HttpSession {

        private final String id;
        private final Map<String, Object> values = new HashMap<>();
        private boolean valid = true;

        public Session(final String id) {
            this.id = id;
        }

        @Override
        public String getId() {
            return id;
        }

        @Override
        public void setAttribute(String name, Object value) {
            checkValid();
            if (value == null) {
                removeAttribute(name);
                return;
            }
            values.put(name, value);
        }

        @Override
        public Object getAttribute(String name) {
            checkValid();
            return values.get(name);
        }

        @Override
        public void removeAttribute(String name) {
            checkValid();
            values.remove(name);
        }

        @Override
        public void invalidate() {
            checkValid();
            SessionManager.getInstance().remove(id);
            values.clear();
            valid = false;
        }

        private void checkValid() {
            if (!valid) {
                throw new IllegalStateException("무효화된 세션입니다.");
            }
        }

        @Override
        public Enumeration<String> getAttributeNames() {
            checkValid();
            return Collections.enumeration(values.keySet());
        }

        @Override
        @Deprecated
        public Object getValue(String name) {
            return getAttribute(name);
        }

        @Override
        @Deprecated
        public String[] getValueNames() {
            checkValid();
            return values.keySet().toArray(new String[0]);
        }

        @Override
        @Deprecated
        public void putValue(String name, Object value) {
            setAttribute(name, value);
        }

        @Override
        @Deprecated
        public void removeValue(String name) {
            removeAttribute(name);
        }

        // 아래 HttpSession API는 이번 로그인 미션에서 사용하지 않는다.
        @Override
        public long getCreationTime() {
            throw new UnsupportedOperationException("세션 생성 시간은 아직 지원하지 않습니다.");
        }

        @Override
        public long getLastAccessedTime() {
            throw new UnsupportedOperationException("세션 접근 시간은 아직 지원하지 않습니다.");
        }

        @Override
        public ServletContext getServletContext() {
            throw new UnsupportedOperationException("ServletContext는 아직 지원하지 않습니다.");
        }

        @Override
        public void setMaxInactiveInterval(int interval) {
            throw new UnsupportedOperationException("세션 만료 시간은 아직 지원하지 않습니다.");
        }

        @Override
        public int getMaxInactiveInterval() {
            throw new UnsupportedOperationException("세션 만료 시간은 아직 지원하지 않습니다.");
        }

        @Override
        @Deprecated
        public HttpSessionContext getSessionContext() {
            throw new UnsupportedOperationException("HttpSessionContext는 지원하지 않습니다.");
        }

        @Override
        public boolean isNew() {
            throw new UnsupportedOperationException("신규 세션 여부는 아직 지원하지 않습니다.");
        }
    }

    public static class SessionManager implements Manager {

        private static final Map<String, HttpSession> SESSIONS = new ConcurrentHashMap<>();
        private static final SessionManager INSTANCE = new SessionManager();

        private SessionManager() {
        }

        public static SessionManager getInstance() {
            return INSTANCE;
        }

        @Override
        public void add(final HttpSession session) {
            SESSIONS.put(session.getId(), session);
        }

        @Override
        public HttpSession findSession(final String id) {
            if (id == null) {
                return null;
            }
            return SESSIONS.get(id);
        }

        @Override
        public void remove(final HttpSession session) {
            remove(session.getId());
        }

        public void remove(final String id) {
            SESSIONS.remove(id);
        }
    }

    public static class HttpCookie {

        private final Map<String, String> cookies = new HashMap<>();

        public HttpCookie(String cookieHeader) {
            for (String cookie : cookieHeader.split(";")) {
                String[] pair = cookie.trim().split("=", 2);
                if (pair.length == 2) {
                    cookies.put(pair[0].trim(), pair[1].trim());
                }
            }
        }

        public String get(String name) {
            return cookies.get(name);
        }
    }

    public enum HttpStatus {

        OK(200, "OK"),
        FOUND(302, "Found"),
        UNAUTHORIZED(401, "Unauthorized"),
        CONFLICT(409, "Conflict");

        int value;
        String message;

        HttpStatus(int value, String message) {
            this.value = value;
            this.message = message;
        }

        public String getHttpStatus() {
            return this.value + " " + this.message;
        }
    }
}
