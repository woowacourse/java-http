package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
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

            final HttpRequest request;
            try {
                request = HttpRequest.readFrom(inputStream);
            } catch (IllegalArgumentException e) {
                return;
            }
            if (request == null) {
                return;
            }

            final String method = request.method();
            final String path = request.path();
            final String httpVersion = request.httpVersion();

            log.info("method: {}, path: {}, version: {}",
                    method, path, httpVersion);

            final String sessionId = findSessionId(request.header("cookie"));
            HttpSession session = sessionManager.findSession(sessionId);
            String setCookieHeader = "";
            if (session == null) {
                setCookieHeader = createSessionCookie(UUID.randomUUID().toString());
            }

            if ("/register".equals(path) && "POST".equals(method)) {
                final String account = request.parameter("account");
                final String password = request.parameter("password");
                final String email = request.parameter("email");

                if (account == null || password == null || email == null) {
                    throw new IOException("회원가입 필수 항목이 누락되었습니다.");
                }

                if (!InMemoryUserRepository.save(new User(account, password, email))) {
                    writeRegistrationConflict(outputStream, setCookieHeader);
                    return;
                }
                writeRedirect(outputStream, "/index.html", setCookieHeader);
                return;
            }

            if ("/login".equals(path) && "POST".equals(method)) {
                final String account = request.parameter("account");
                final String password = request.parameter("password");

                final User user = account == null || password == null
                        ? null
                        : InMemoryUserRepository.findByAccount(account)
                                .filter(candidate -> candidate.checkPassword(password))
                                .orElse(null);

                if (user != null) {
                    if (session != null) {
                        invalidateSession(session);
                    }
                    session = createSession();
                    session.setAttribute("user", user);
                    setCookieHeader = createSessionCookie(session.getId());
                    log.info("회원 조회 성공: {}", account);
                }

                final String location = user != null ? "/index.html" : "/401.html";
                writeRedirect(outputStream, location, setCookieHeader);
                return;
            }

            if ("GET".equals(method) && "/login".equals(path)) {
                if (isLoggedIn(session)) {
                    writeRedirect(outputStream, "/index.html", setCookieHeader);
                    return;
                }
                if (session != null) {
                    setCookieHeader = createSessionCookie(UUID.randomUUID().toString());
                }
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

    private boolean isLoggedIn(HttpSession session) {
        if (session == null) {
            return false;
        }
        try {
            return session.getAttribute("user") instanceof User;
        } catch (IllegalStateException e) {
            return false;
        }
    }

    private void invalidateSession(HttpSession session) {
        try {
            session.invalidate();
        } catch (IllegalStateException e) {
            log.debug("이미 무효화된 세션입니다.");
        }
    }

    private HttpSession createSession() {
        final HttpSession session = new Session(UUID.randomUUID().toString(), sessionManager);
        sessionManager.add(session);
        return session;
    }

    private String createSessionCookie(String sessionId) {
        return "Set-Cookie: JSESSIONID=" + sessionId + "; Path=/\r\n";
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

    private void writeRegistrationConflict(OutputStream output, String setCookieHeader)
            throws IOException {
        final byte[] body = "이미 사용 중인 계정입니다.".getBytes(StandardCharsets.UTF_8);
        final String header = String.join("\r\n",
                "HTTP/1.1 409 Conflict",
                "Content-Type: text/plain;charset=utf-8",
                "Content-Length: " + body.length)
                + "\r\n" + setCookieHeader + "\r\n";
        output.write(header.getBytes(StandardCharsets.UTF_8));
        output.write(body);
        output.flush();
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
