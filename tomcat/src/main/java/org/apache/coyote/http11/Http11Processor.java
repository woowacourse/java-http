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
            HttpRequest request = new HttpRequest(inputStream);

            String response = getResponse(request);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponse(HttpRequest request) throws IOException {
        try {
            if (request.equalPath("/")) {
                if (request.equalMethod(HttpMethod.GET)) {
                    return create200Response("Hello world!", ContentType.TEXT_PLAIN);
                }
            }
            if (request.equalPath("/login")) {
                if (request.equalMethod(HttpMethod.GET)) {
                    if (request.getSession() != null) {
                        return create302Response("/index.html");
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
            return create404Response();
        } catch (RuntimeException e) {
            log.error(e.getMessage(), e);
            return create500Response();
        }
    }

    private String createStaticResourceResponse(String path) throws IOException {
        Path staticResource = getStaticResource(path);
        if (staticResource == null) {
            return create404Response();
        }
        String fileExtension = path.split("\\.")[1];
        return create200Response(Files.readString(staticResource), ContentType.of(fileExtension));
    }

    private String login(HttpRequest request) {
        Optional<User> optionalUser = InMemoryUserRepository.findByAccount(request.getBody("account"));
        if (optionalUser.isEmpty()) {
            log.info("존재하지 않는 유저입니다.");
            return create302Response("/401.html");
        }
        User user = optionalUser.get();
        if (!user.checkPassword(request.getBody("password"))) {
            log.info("비밀번호가 일치하지 않습니다.");
            return create302Response("/401.html");
        }
        log.info(user.toString());
        if (request.getSession() == null) {
            Session session = createSession(user);
            return create302LoginResponse("/index.html", session.getId());
        }
        return create302Response("/index.html");
    }

    private String register(HttpRequest request) {
        User user = new User(request.getBody("account"), request.getBody("password"), request.getBody("email"));
        InMemoryUserRepository.save(user);
        Session session = createSession(user);
        return create302LoginResponse("/index.html", session.getId());
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

    private String create200Response(String body, ContentType contentType) throws IOException {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType.getMimeType() + " ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String create302Response(String path) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + path,
                "");
    }

    private String create302LoginResponse(String path, String sessionId) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Set-Cookie: JSESSIONID=" + sessionId,
                "Location: " + path,
                "");
    }

    private String create404Response() throws IOException {
        String body = Files.readString(getStaticResource("/404.html"));
        return String.join("\r\n",
                "HTTP/1.1 404 NOT FOUND ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }

    private String create500Response() throws IOException {
        String body = Files.readString(getStaticResource("/500.html"));
        return String.join("\r\n",
                "HTTP/1.1 500 Internal Server Error ",
                "Content-Type: text/html;charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " ",
                "",
                body);
    }
}
