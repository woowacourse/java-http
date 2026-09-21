package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.session.Session;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var input = new BufferedInputStream(inputStream);
            final String requestLine = readHttpLine(input);

            if (requestLine == null) {
                return;
            }

            final String[] parts = requestLine.split(" ");
            if (parts.length != 3) {
                return;
            }

            final String method = parts[0];
            final String requestTarget = parts[1];
            final String[] targetParts = requestTarget.split("\\?", 2);
            final String path = targetParts[0];
            final String httpVersion = parts[2];

            log.info("method: {}, path: {}, version: {}",
                    method, path, httpVersion);

            final Map<String, String> headers = readHeaders(input);
            final String body = readBody(input, headers);
            final String sessionId = findSessionId(headers.get("cookie"));
            HttpSession session = sessionManager.findSession(sessionId);
            String setCookieHeader = "";
            if (session == null) {
                session = createSession();
                setCookieHeader = createSessionCookie(session);
            }

            if ("/register".equals(path) && "POST".equals(method)) {
                final Map<String, String> parameters = parseQuery(body);
                final String account = parameters.get("account");
                final String password = parameters.get("password");
                final String email = parameters.get("email");

                if (account == null || password == null || email == null) {
                    throw new IOException("회원가입 필수 항목이 누락되었습니다.");
                }

                InMemoryUserRepository.save(new User(account, password, email));
                writeRedirect(outputStream, "/index.html", setCookieHeader);
                return;
            }

            if ("/login".equals(path) && "POST".equals(method)) {
                final Map<String, String> parameters = parseQuery(body);

                final String account = parameters.get("account");
                final String password = parameters.get("password");

                final User user = account == null || password == null
                        ? null
                        : InMemoryUserRepository.findByAccount(account)
                                .filter(candidate -> candidate.checkPassword(password))
                                .orElse(null);

                if (user != null) {
                    session.invalidate();
                    session = createSession();
                    session.setAttribute("user", user);
                    setCookieHeader = createSessionCookie(session);
                    log.info("회원 조회 성공: {}", account);
                }

                final String location = user != null ? "/index.html" : "/401.html";
                writeRedirect(outputStream, location, setCookieHeader);
                return;
            }

            if ("GET".equals(method) && "/login".equals(path)
                    && session.getAttribute("user") instanceof User) {
                writeRedirect(outputStream, "/index.html", setCookieHeader);
                return;
            }

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            String contentType = "text/html;charset=utf-8";

            if ("/index.html".equals(path)
                    || "/401.html".equals(path)
                    || "/css/styles.css".equals(path)
                    || path.endsWith(".js")
                    || "/login".equals(path)
                    || "/register".equals(path)) {

                if ("/css/styles.css".equals(path)) {
                    contentType = "text/css;charset=utf-8";
                } else if (path.endsWith(".js")) {
                    contentType = "text/javascript;charset=utf-8";
                }

                final String resourcePath = "/login".equals(path) || "/register".equals(path)
                        ? "static" + path + ".html"
                        : "static" + path;

                responseBody = readResource(resourcePath);
            }
            final String responseHeader = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.length + " ")
                    + "\r\n" + setCookieHeader + "\r\n";

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IllegalArgumentException e) {
            log.warn("잘못된 쿼리 인코딩입니다.");
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpSession createSession() {
        final HttpSession session = new Session(UUID.randomUUID().toString(), sessionManager);
        sessionManager.add(session);
        return session;
    }

    private String createSessionCookie(HttpSession session) {
        return "Set-Cookie: JSESSIONID=" + session.getId() + "; Path=/\r\n";
    }

    private String findSessionId(String cookieHeader) {
        if (cookieHeader == null) {
            return null;
        }

        for (String cookie : cookieHeader.split(";")) {
            final String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2
                    && "JSESSIONID".equals(pair[0].trim())
                    && !pair[1].isBlank()) {
                return pair[1].trim();
            }
        }
        return null;
    }

    private void writeRedirect(OutputStream output, String location,
                               String setCookieHeader) throws IOException {
        final String response = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0")
                + "\r\n" + setCookieHeader + "\r\n";

        output.write(response.getBytes(StandardCharsets.UTF_8));
        output.flush();
    }

    private String readHttpLine(InputStream input) throws IOException {
        final var bytes = new ByteArrayOutputStream();
        int value;

        while ((value = input.read()) != -1) {
            if (value == '\n') {
                final String line = bytes.toString(StandardCharsets.UTF_8);
                return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
            }
            bytes.write(value);
        }

        return bytes.size() == 0 ? null : bytes.toString(StandardCharsets.UTF_8);
    }

    private Map<String, String> readHeaders(InputStream input) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = readHttpLine(input)) != null) {
            if (line.isEmpty()) {
                return headers;
            }

            final String[] parts = line.split(":", 2);
            if (parts.length != 2) {
                throw new IOException("잘못된 요청 헤더입니다.");
            }
            headers.put(parts[0].trim().toLowerCase(Locale.ROOT), parts[1].trim());
        }

        throw new IOException("요청 헤더가 완전히 도착하지 않았습니다.");
    }

    private String readBody(InputStream input, Map<String, String> headers) throws IOException {
        final int contentLength;
        try {
            contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        } catch (NumberFormatException e) {
            throw new IOException("잘못된 Content-Length입니다.");
        }

        if (contentLength < 0) {
            throw new IOException("잘못된 Content-Length입니다.");
        }

        final byte[] body = input.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new IOException("요청 본문이 완전히 도착하지 않았습니다.");
        }

        return new String(body, StandardCharsets.UTF_8);
    }

    private Map<String, String> parseQuery(String query) {
        final Map<String, String> parameters = new HashMap<>();

        for (String parameter : query.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length != 2) {
                continue;
            }

            final String key = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            parameters.put(key, value);
        }

        return parameters;
    }

    private byte[] readResource(String resourcePath) throws IOException {
        try (var resource = getClass().getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (resource == null) {
                throw new IOException(resourcePath + " 파일을 찾을 수 없습니다.");
            }

            return resource.readAllBytes();
        }
    }
}
