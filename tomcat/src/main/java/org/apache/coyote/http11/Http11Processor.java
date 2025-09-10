package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

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
             final var outputStream = connection.getOutputStream()) {
            try {
                HttpRequest request = new HttpRequest(inputStream);
                HttpResponse response = getResponse(request);

                outputStream.write(response.getBytes());
                outputStream.flush();
            } catch (IOException e) {
                byte[] body = Files.readAllBytes(getStaticResource("/400.html"));
                Map<String, String> headers = Map.of("Content-Type", "text/html;charset=utf-8", "Content-Length", String.valueOf(body.length));
                HttpResponse response = new HttpResponse(HttpStatus.BAD_REQUEST, headers, body);
                outputStream.write(response.getBytes());
                outputStream.flush();
            }
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(HttpRequest request) throws IOException {
        try {
            if (request.equalPath("/")) {
                if (request.equalMethod(HttpMethod.GET)) {
                    byte[] body = "Hello world!".getBytes();
                    Map<String, String> headers = Map.of("Content-Type", ContentType.TEXT_PLAIN.getMimeType(), "Content-Length", String.valueOf(body.length));
                    return new HttpResponse(HttpStatus.OK, headers, body);
                }
            }
            if (request.equalPath("/login")) {
                if (request.equalMethod(HttpMethod.GET)) {
                    if (request.getSession() != null) {
                        Map<String, String> headers = Map.of("Location", "/index.html");
                        return new HttpResponse(HttpStatus.FOUND, headers, null);

                    }
                    return createStaticResourceResponse("/login.html");
                }
                if (request.equalMethod(HttpMethod.POST)) {
                    return login(request);
                }
            }
            if (request.equalPath("/register")) {
                if (request.equalMethod(HttpMethod.GET)) {
                    return createStaticResourceResponse("/register.html");
                }
                if (request.equalMethod(HttpMethod.POST)) {
                    return register(request);
                }
            }
            if (request.isStaticResourcePath()) {
                if (request.equalMethod(HttpMethod.GET)) {
                    return createStaticResourceResponse(request.getPath());
                }
            }
            byte[] body = Files.readAllBytes(getStaticResource("/404.html"));
            Map<String, String> headers = Map.of("Content-Type", "text/html;charset=utf-8", "Content-Length", String.valueOf(body.length));
            return new HttpResponse(HttpStatus.NOT_FOUND, headers, body);
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            byte[] body = Files.readAllBytes(getStaticResource("/500.html"));
            Map<String, String> headers = Map.of("Content-Type", "text/html;charset=utf-8", "Content-Length", String.valueOf(body.length));
            return new HttpResponse(HttpStatus.INTERNAL_SERVER_ERROR, headers, body);
        }
    }

    private HttpResponse createStaticResourceResponse(String path) throws IOException {
        Path staticResource = getStaticResource(path);
        if (staticResource == null) {
            byte[] body = Files.readAllBytes(getStaticResource("/404.html"));
            Map<String, String> headers = Map.of("Content-Type", "text/html;charset=utf-8", "Content-Length", String.valueOf(body.length));
            return new HttpResponse(HttpStatus.NOT_FOUND, headers, body);
        }
        String fileExtension = path.split("\\.")[1];
        byte[] body = Files.readAllBytes(staticResource);
        Map<String, String> headers = Map.of("Content-Type", ContentType.of(fileExtension).getMimeType(), "Content-Length", String.valueOf(body.length));
        return new HttpResponse(HttpStatus.OK, headers, body);
    }

    private HttpResponse login(HttpRequest request) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(request.getBody("account"));
        if (optionalUser.isEmpty()) {
            log.info("존재하지 않는 유저입니다.");
            Map<String, String> headers = Map.of("Location", "/401.html");
            return new HttpResponse(HttpStatus.FOUND, headers, null);
        }
        User user = optionalUser.get();
        if (!user.checkPassword(request.getBody("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            Map<String, String> headers = Map.of("Location", "/401.html");
            return new HttpResponse(HttpStatus.FOUND, headers, null);
        }
        log.info(user.toString());
        if (request.getSession() == null) {
            Session session = createSession(user);
            Map<String, String> headers = Map.of("Set-Cookie", "JSESSIONID=" + session.getId(), "Location", "/index.html");
            return new HttpResponse(HttpStatus.FOUND, headers, null);
        }
        Map<String, String> headers = Map.of("Location", "/index.html");
        return new HttpResponse(HttpStatus.FOUND, headers, null);
    }

    private HttpResponse register(HttpRequest request) {
        User user = new User(request.getBody("account"), request.getBody("password"), request.getBody("email"));
        InMemoryUserRepository.save(user);
        Session session = createSession(user);
        Map<String, String> headers = Map.of("Set-Cookie", "JSESSIONID=" + session.getId(), "Location", "/index.html");
        return new HttpResponse(HttpStatus.FOUND, headers, null);
    }

    private Session createSession(User user) {
        Session session = new Session();
        session.setAttribute("user", user);
        SessionManager.getInstance().add(session);
        return session;
    }

    private Path getStaticResource(String url) {
        URL resourceURL = getClass().getClassLoader().getResource("static" + url);
        if (resourceURL == null) {
            return null;
        }
        return Path.of(resourceURL.getFile());
    }
}
