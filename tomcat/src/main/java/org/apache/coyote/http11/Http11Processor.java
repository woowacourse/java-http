package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.RequestLine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final Manager sessionManager;

    public Http11Processor(final Socket connection, final Manager sessionManager) {
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
        try (
                final var inputStream = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream()
        ) {
            final RequestLine requestLine = new RequestLine(readLine(inputStream));
            final Headers headers = readHeaders(inputStream);
            final Cookies cookies = new Cookies(headers.cookie());
            final Session session = getSession(cookies);

            String path = requestLine.getPath();
            String code = "200";
            String status = "OK";

            if (requestLine.isPost()) {
                final int contentLength = headers.contentLength();
                final byte[] buffer = inputStream.readNBytes(contentLength);
                final String requestBody = new String(buffer, StandardCharsets.UTF_8);
                final Map<String, String> parameters = parseQueryString(requestBody);

                if ("/register".equals(path)) {
                    register(parameters);
                    path = "/index";
                    code = "200";
                    status = "OK";
                } else if ("/login".equals(path)) {
                    if (login(parameters, session)) {
                        path = "/index";
                        code = "302";
                        status = "FOUND";
                    } else {
                        path = "/401";
                        code = "401";
                        status = "UNAUTHORIZED";
                    }
                }
                path += ".html";
            }
            if (requestLine.isGet()) {
                if ("/login".equals(path) && session.getAttribute("user") != null) {
                    path = "/";
                    code = "302";
                    status = "FOUND";
                }
                path = resolveGetPath(path);
            }

            final var response = makeResponse(path, code, status, session.getId(), cookies.getSessionId());
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String resolveGetPath(final String requestPath) {
        return switch (requestPath) {
            case "/" -> "/index.html";
            case "/login" -> "/login.html";
            case "/register" -> "/register.html";
            default -> requestPath;
        };
    }

    private Session getSession(final Cookies cookies) throws IOException {
        String sessionId = cookies.getSessionId();
        if (sessionId == null) {
            Session session = new Session(UUID.randomUUID().toString());
            sessionManager.add(session);
            return session;
        }

        Session session = sessionManager.findSession(sessionId);
        if (session == null) {
            session = new Session(sessionId);
            sessionManager.add(session);
            return session;
        }

        return session;
    }

    private String makeResponse(
            final String path,
            final String code,
            final String status,
            final String sessionId,
            final String sessionIdFromCookie
    ) throws IOException {
        final String responseBody = getResponseBody(path);
        final String contentType = resolveContentType(path);

        final List<String> responseLines = new ArrayList<>();
        responseLines.add("HTTP/1.1 " + code + " " + status);
        responseLines.add("Content-Type: " + contentType + ";charset=utf-8");
        responseLines.add("Content-Length: " + responseBody.getBytes().length);
        if (sessionIdFromCookie == null) {
            responseLines.add("Set-Cookie: JSESSIONID=" + sessionId);
        }
        responseLines.add("");
        responseLines.add(responseBody);

        return String.join("\r\n", responseLines);
    }

    private String getResponseBody(final String requestPath) throws IOException {
        final String resourceName = "static" + requestPath;
        final String resourcePath = Objects.requireNonNull(
                getClass().getClassLoader().getResource(resourceName),
                "리소스를 찾을 수 없음: " + resourceName
        ).getPath();

        return Files.readString(Path.of(resourcePath));
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();
        if (queryString.isBlank()) {
            return parameters;
        }

        for (String pair : queryString.split("&")) {
            final String[] nameAndValue = pair.split("=", 2);
            if (nameAndValue.length != 2) {
                continue;
            }
            parameters.put(nameAndValue[0].trim(), nameAndValue[1].trim());
        }
        return parameters;
    }

    private boolean login(final Map<String, String> parameters, final Session session) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        final Optional<User> loginUser = InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
        if (loginUser.isEmpty()) {
            return false;
        }

        final User user = loginUser.get();
        log.info("로그인 성공: {}", user);
        session.setAttribute("user", user);

        return true;
    }

    private void register(final Map<String, String> parameters) {
        final User user = new User(
                parameters.get("account"),
                parameters.get("password"),
                parameters.get("email")
        );

        InMemoryUserRepository.save(user);
        log.info("회원가입 성공: {}", user);
    }

    private static Headers readHeaders(final BufferedInputStream inputStream) throws IOException {
        final Headers headers = new Headers();

        String line = readLine(inputStream);
        while (!"".equals(line)) {
            if (line == null) {
                throw new IllegalArgumentException("헤더가 올바르지 않습니다.");
            }
            headers.add(line);
            line = readLine(inputStream);
        }
        return headers;
    }

    private static String readLine(final BufferedInputStream inputStream) throws IOException {
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

        int value;
        while ((value = inputStream.read()) != -1 && value != '\n') {
            buffer.write(value);
        }
        if (value == -1 && buffer.size() == 0) {
            return null;
        }

        final byte[] bytes = buffer.toByteArray();
        final int length = (bytes.length > 0) && (bytes[bytes.length - 1] == '\r')
                ? bytes.length - 1
                : bytes.length;
        return new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
    }
}
