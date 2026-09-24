package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_RESOURCE_PATH = "/login.html";
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
            HttpRequest request = HttpRequest.readFrom(inputStream);
            if (request == null) {
                return;
            }
            SessionManager sessionManager = new SessionManager();
            Optional<Session> existingSession = request.findCookie("JSESSIONID")
                    .flatMap(sessionManager::findSession);
            Session session = existingSession.orElseGet(() -> {
                Session newSession = new Session(UUID.randomUUID().toString());
                sessionManager.add(newSession);
                return newSession;
            });
            HttpResponse response = route(request, session);
            if (existingSession.isEmpty() && !response.hasHeader("Set-Cookie")) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
            }
            outputStream.write(response.toByteArray());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse route(HttpRequest request, Session session) throws IOException, URISyntaxException {
        if (request.matches("POST", LOGIN_PATH)) {
            return login(request, session);
        }
        if (request.matches("POST", "/register")) {
            return register(request);
        }
        if (request.matches("GET", LOGIN_PATH) && session.getAttribute("user") instanceof User) {
            return HttpResponse.redirectTo("/index.html");
        }
        return handleResourceRequest(request);
    }

    private HttpResponse register(HttpRequest request) {
        String account = request.findFormParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
        String password = request.findFormParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));
        String email = request.findFormParameter("email")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: email"));
        InMemoryUserRepository.save(new User(account, password, email));
        return HttpResponse.redirectTo("/index.html");
    }

    private HttpResponse login(HttpRequest request, Session session) {
        String account = request.findFormParameter("account")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
        String password = request.findFormParameter("password")
                .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));

        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        user.ifPresent(value -> log.info("user : {}", value));
        Optional<User> authenticatedUser = user.filter(value -> value.checkPassword(password));
        if (authenticatedUser.isEmpty()) {
            return HttpResponse.redirectTo("/401.html");
        }

        session.setAttribute("user", authenticatedUser.get());

        HttpResponse response = HttpResponse.redirectTo("/index.html");
        response.addHeader("Set-Cookie", "JSESSIONID=" + session.getId());
        return response;
    }

    private HttpResponse handleResourceRequest(HttpRequest request) throws IOException, URISyntaxException {
        String resourcePath = resolveResourcePath(request.getPath());
        byte[] responseBody = ROOT_RESPONSE_BODY.getBytes();
        if (!resourcePath.equals("/")) {
            String fileName = STATIC_RESOURCE_PREFIX + resourcePath;
            URL resource = getClass().getClassLoader().getResource(fileName);
            if (resource != null) {
                Path path = Paths.get(resource.toURI());
                responseBody = Files.readAllBytes(path);
            }
        }
        String contentType = contentTypeOf(request.getExtension());
        return new HttpResponse("200 OK", contentType, responseBody);
    }

    private String resolveResourcePath(String requestPath) {
        if (requestPath.equals(LOGIN_PATH)) {
            return LOGIN_RESOURCE_PATH;
        } else if (requestPath.equals("/register")) {
            return "/register.html";
        }
        return requestPath;
    }

    private String contentTypeOf(String extension) {
        if (extension.equals("css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
