package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Manager SESSION_MANAGER = SessionManager.getInstance();

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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final HttpRequest httpRequest = HttpRequestParser.parse(reader);
            if (httpRequest == null) {
                return;
            }

            final String method = httpRequest.getMethod();
            final String path = httpRequest.getPath();
            final String body = httpRequest.getBody();
            final String cookieHeader = httpRequest.getHeader("Cookie");
            final HttpCookie cookie = cookieHeader == null ? null : new HttpCookie(cookieHeader);

            String jSessionId = null;
            if (cookie != null) {
                jSessionId = cookie.getJsessionId();
            }
            boolean shouldSetCookie = jSessionId == null;
            if (shouldSetCookie) {
                jSessionId = UUID.randomUUID().toString();
            }

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);

            if ("/login".equals(path) && "GET".equals(method) && isLoggedIn(jSessionId)) {
                StringBuilder responseHeader = new StringBuilder();
                responseHeader.append("HTTP/1.1 302 Found").append("\r\n");
                appendSetCookieIfMissing(responseHeader, shouldSetCookie, jSessionId);
                responseHeader.append("Location: /index.html").append("\r\n");
                responseHeader.append("Content-Length: 0").append("\r\n\r\n");
                outputStream.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            // 로그인 success/fail
            if ("/login".equals(path) && "POST".equals(method)) {
                final String[] parameters = body.split("&");
                String account = parameters[0].split("=")[1];
                String password = parameters[1].split("=")[1];

                final User user = InMemoryUserRepository.findByAccount(account)
                        .filter(foundUser -> foundUser.checkPassword(password))
                        .orElse(null);

                if (user != null) {
                    HttpSession session = SESSION_MANAGER.findSession(jSessionId);
                    if (session == null) {
                        if (!shouldSetCookie) {
                            jSessionId = UUID.randomUUID().toString();
                            shouldSetCookie = true;
                        }
                        session = new Session(jSessionId);
                        SESSION_MANAGER.add(session);
                    }
                    session.setAttribute("user", user);

                    StringBuilder responseHeader = new StringBuilder();
                    responseHeader.append("HTTP/1.1 302 Found").append("\r\n");
                    appendSetCookieIfMissing(responseHeader, shouldSetCookie, jSessionId);
                    responseHeader.append("Location: /index.html").append("\r\n");
                    responseHeader.append("Content-Length: 0").append("\r\n\r\n");
                    outputStream.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
                    outputStream.flush();
                    return;
                }
                StringBuilder responseHeader = new StringBuilder();
                responseHeader.append("HTTP/1.1 302 Found").append("\r\n");
                appendSetCookieIfMissing(responseHeader, shouldSetCookie, jSessionId);
                responseHeader.append("Location: /401.html").append("\r\n");
                responseHeader.append("Content-Length: 0").append("\r\n\r\n");

                outputStream.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            // 회원가입 처리
            if ("/register".equals(path) && "POST".equals(method)) {
                String[] parameters = body.split("&");
                String account = parameters[0].split("=")[1];
                String email = URLDecoder.decode(parameters[1].split("=")[1], StandardCharsets.UTF_8);
                String password = parameters[2].split("=")[1];
                InMemoryUserRepository.save(new User(account, password, email));

                StringBuilder responseHeader = new StringBuilder();
                responseHeader.append("HTTP/1.1 302 Found").append("\r\n");
                appendSetCookieIfMissing(responseHeader, shouldSetCookie, jSessionId);
                responseHeader.append("Location: /index.html").append("\r\n");
                responseHeader.append("Content-Length: 0").append("\r\n\r\n");

                outputStream.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            // login 페이지 반환 및 이외
            if (!path.equals("/")) {
                String resourcePath = path;
                if(path.equals("/login")) {
                    resourcePath = "/login.html";
                }
                if(path.equals("/register")) {
                    resourcePath = "/register.html";
                }
                try (final InputStream resourceStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream("static" + resourcePath)) {

                    if (resourceStream == null) {
                        throw new IllegalArgumentException("리소스를 찾을 수 없습니다: " + "static" + resourcePath);
                    }
                    responseBody = resourceStream.readAllBytes();
                }
            }

            final String contentType = getContentType(path);

            StringBuilder responseHeader = new StringBuilder();
            responseHeader.append("HTTP/1.1 200 OK \r\n");
            appendSetCookieIfMissing(responseHeader, shouldSetCookie, jSessionId);
            responseHeader.append("Content-Type: ").append(contentType).append("\r\n");
            responseHeader.append("Content-Length: ").append(responseBody.length).append(" \r\n\r\n");

            outputStream.write(responseHeader.toString().getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8 ";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }

    private boolean isLoggedIn(final String jSessionId) throws IOException {
        final HttpSession session = SESSION_MANAGER.findSession(jSessionId);
        return session != null && session.getAttribute("user") != null;
    }

    private void appendSetCookieIfMissing(final StringBuilder responseHeader, final boolean shouldSetCookie,
                                          final String jSessionId) {
        if (shouldSetCookie) {
            responseHeader.append("Set-Cookie: JSESSIONID=")
                    .append(jSessionId)
                    .append("\r\n");
        }
    }
}
