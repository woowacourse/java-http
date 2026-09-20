package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.request.line.HttpMethod;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.headers.ContentType;
import org.apache.coyote.http11.session.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final SessionManager sessionManager) {
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

            HttpRequest request = HttpRequest.from(inputStream);
            Session session = sessionManager.findSession(request.getCookie("JSESSIONID"));

            String path = request.getPath();
            if (request.getHttpMethod().equals(HttpMethod.GET) && request.getPath().equals("/register")) {
                // FIXME HttpResponse.ok 형식 중복
                path = "/register.html";
                URL resourcePath = getClass().getClassLoader().getResource("static" + path);
                final var contentType = ContentType.from(path);
                final var content = Files.readString(Path.of(resourcePath.toURI()), StandardCharsets.UTF_8);
                HttpResponse httpResponse = HttpResponse.ok(contentType, content);
                httpResponse.write(outputStream);
                return;
            }

            if (request.getHttpMethod().equals(HttpMethod.POST) && request.getPath().equals("/register")) {
                String account = request.getBodyValue("account");
                String password = request.getBodyValue("password");
                String email = request.getBodyValue("email");

                User user = new User(account, password, email);
                InMemoryUserRepository.save(user);

                // FIXME HttpResponse.redirect 형식 중복
                HttpResponse httpResponse = HttpResponse.redirect("/index.html");
                httpResponse.write(outputStream);
                return;
            }

            // THINK 반복되는 엔드포인트 매핑 리팩터링 - P1
            if (request.getHttpMethod().equals(HttpMethod.GET)
                    && (request.getPath().equals("/login") || request.getPath().equals("/login.html"))) {
                if (session != null && session.getAttribute("user") != null) {
                    // FIXME HttpResponse.redirect 형식 중복
                    HttpResponse httpResponse = HttpResponse.redirect("/index.html");
                    httpResponse.write(outputStream);
                    return;
                }

                // FIXME HttpResponse.ok 형식 중복
                path = "/login.html";
                URL resourcePath = getClass().getClassLoader().getResource("static" + path);
                final var contentType = ContentType.from(path);
                final var content = Files.readString(Path.of(resourcePath.toURI()), StandardCharsets.UTF_8);
                HttpResponse httpResponse = HttpResponse.ok(contentType, content);
                httpResponse.write(outputStream);
                return;
            }

            if (request.getHttpMethod().equals(HttpMethod.POST) && request.getPath().equals("/login")) {
                String account = request.getBodyValue("account");
                String password = request.getBodyValue("password");
                Optional<User> loginedUser = InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.checkPassword(password));

                if (loginedUser.isPresent()) {
                    UUID sessionId = UUID.randomUUID();
                    HttpCookie cookie = new HttpCookie("JSESSIONID=" + sessionId);
                    Session newSession = new Session(sessionId.toString());
                    newSession.setAttribute("user", loginedUser.get());
                    sessionManager.add(newSession);

                    // FIXME HttpResponse.redirect 형식 중복
                    HttpResponse httpResponse = HttpResponse.redirect("/index.html", cookie);
                    httpResponse.write(outputStream);
                    return;
                } else {
                    // FIXME HttpResponse.redirect 형식 중복
                    HttpResponse httpResponse = HttpResponse.redirect("/401.html");
                    httpResponse.write(outputStream);
                    return;
                }
            }

            if (request.getPath().equals("/")) {
                // FIXME HttpResponse.ok 형식 중복
                path = "/index.html";
                URL resourcePath = getClass().getClassLoader().getResource("static" + path);
                final var contentType = ContentType.from(path);
                final var content = Files.readString(Path.of(resourcePath.toURI()), StandardCharsets.UTF_8);
                HttpResponse httpResponse = HttpResponse.ok(contentType, content);
                httpResponse.write(outputStream);
                return;
            }

            // FIXME HttpResponse.ok 형식 중복
            URL resourcePath = getClass().getClassLoader().getResource("static" + path); // 여기까지 온 path는 잘못된 경로일 수 있으므로 검증.
            if (resourcePath == null) {
                final var notFoundResponse = getResponseBody("404 Not Found", "text/html", "<h1>404 Not Found</h1>");
                outputStream.write(notFoundResponse.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final var contentType = ContentType.from(path);
            final var content = Files.readString(Path.of(resourcePath.toURI()), StandardCharsets.UTF_8);
            HttpResponse httpResponse = HttpResponse.ok(contentType, content);
            httpResponse.write(outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            log.error("유효하지 않은 URI : {}", e.getMessage(), e);
        }
    }

    // THINK: 추후 응답 관련 내용을 응집화한 HttpResponse으로 포장 - P0
    public String getResponseBody(String status, String contentType, String content) {
        byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Content-Type: " + contentType + ";charset=utf-8",
                "Content-Length: " + bytes.length,
                "",
                content);
    }

    public String redirect(String status, String location) {
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Location: " + location,
                "Content-Length: 0",
                "");
    }

    public String redirect(String status, String location, HttpCookie cookie) {
        log.info("Cookie : {}", cookie.serialize());
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                "Location: " + location,
                "Set-Cookie: " + cookie.serialize(),
                "Content-Length: 0",
                "");
    }

    private String contentTypeOf(String path) {
        int dotIndex = path.lastIndexOf('.');
        String extension = (dotIndex == -1) ? "" : path.substring(dotIndex);

        return switch (extension) {
            case ".css" -> "text/css";
            case ".js" -> "text/javascript";
            case ".ico" -> "image/x-icon";
            default -> "text/html";
        };
    }

    private String readStaticResource(String path) throws IOException, URISyntaxException {
        URL resource = getClass().getClassLoader().getResource("static" + path);
        if (resource == null) {
            return null;
        }
        return Files.readString(Path.of(resource.toURI()), StandardCharsets.UTF_8);
    }

}
