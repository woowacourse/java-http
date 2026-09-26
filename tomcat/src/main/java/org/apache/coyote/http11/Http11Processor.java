package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

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
             final var outputStream = connection.getOutputStream();
             final BufferedReader bufferedReader = new BufferedReader(
                     new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final HttpRequest request = HttpRequest.from(bufferedReader);
            final String requestMethod = request.getMethod();
            final String path = request.getPath();
            final HttpResponse response;

            if ("POST".equals(requestMethod) && "/register".equals(path)) {
                register(request.getParameters());
                response = HttpResponse.redirect("/index.html");
            } else if ("POST".equals(requestMethod) && "/login".equals(path)) {
                final Optional<User> user = findLoginUser(request.getParameters());
                if (user.isPresent()) {
                    final Session session = new Session(UUID.randomUUID().toString());
                    session.setAttribute("user", user.get());
                    SessionManager.INSTANCE.add(session);
                    response = HttpResponse.redirectWithCookie("/index.html", session.getId());
                } else {
                    response = HttpResponse.redirect("/401.html");
                }
            } else if ("GET".equals(requestMethod) && "/login".equals(path) && isLoggedIn(request)) {
                response = HttpResponse.redirect("/index.html");
            } else if ("/".equals(path)) {
                response = HttpResponse.ok("text/html", "Hello world!");
            } else {
                response = HttpResponse.ok(resolveContentType(path), readStaticFile(path));
            }

            outputStream.write(response.format().getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException | IllegalArgumentException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private void register(final Map<String, String> requestParams) {
        final String account = requestParams.get("account");
        final String password = requestParams.get("password");
        final String email = requestParams.get("email");
        final User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private Optional<User> findLoginUser(final Map<String, String> queryParams) {
        final String account = queryParams.get("account");
        final String password = queryParams.get("password");

        if (account == null) {
            return Optional.empty();
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(u -> u.checkPassword(password));
        user.ifPresent(u -> log.info("로그인 성공! user: {}", u));

        return user;
    }

    private boolean isLoggedIn(final HttpRequest request) {
        final HttpCookie cookie = new HttpCookie(request.getHeader("Cookie"));
        final String sessionId = cookie.get("JSESSIONID");

        if (sessionId == null) {
            return false;
        }

        final Session session = SessionManager.INSTANCE.findSession(sessionId);

        if (session == null) {
            return false;
        } else if (session.getAttribute("user") == null) {
            return false;
        } else {
            return true;
        }
    }

    private String readStaticFile(final String path) throws URISyntaxException, IOException {
        final String fileName = "static" + resolveFileName(path);
        final URL url = ClassLoader.getSystemResource(fileName); // 클래스패스에서 static/ 아래 파일을 찾아 실제 위치를 URL로 돌려 줌
        final File file = new File(url.toURI());
        return new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
    }

    // 요청 path를 받아서, 서버에서 찾을 파일 이름을 돌려 줌
    private String resolveFileName(final String path) {
        if (!path.contains(".")) {
            return path + ".html";
        }
        return path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
