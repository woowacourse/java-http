package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nullable;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestParser httpRequestParser;
    private final SessionManager sessionManager;

    public Http11Processor(final Socket connection, final SessionManager sessionManager) {
        this.connection = connection;
        httpRequestParser = new HttpRequestParser();
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
            final var request = httpRequestParser.accept(inputStream);
            final var response = handleRequest(request);

            outputStream.write(response.buildResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(final HttpRequest request) throws IOException {
        final var uri = request.getUri();
        if (uri.equals("/")) {
            return handleStaticResponse("/");
        }
        if (uri.equals("/login")) {
            return handleLogin(request);
        }
        if (uri.equals("/register")) {
            if (request.isPost()) {
                return postRegister(request);
            }
            return handleStaticResponse("/register.html");
        }
        return handleStaticResponse(uri);
    }

    private HttpResponse handleStaticResponse(String uri) throws IOException {
        try {
            return buildStaticResponse("200 OK", uri);
        } catch (Exception e) {
            return buildStaticResponse("404 Not Found", "/404.html");
        }
    }

    private HttpResponse buildStaticResponse(String status, String uri) throws IOException {
        File page = getFile(uri);
        String contentType = getContentType(page);
        String body = buildResponseBody(page);
        return new HttpResponse(status, contentType, body);
    }

    @Nullable
    private File getFile(final String uri) {
        if (uri.equals("/")) {
            return null;
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return Optional.ofNullable(resource.getFile()).map(File::new).orElse(null);
    }

    private String getContentType(final File file) throws IOException {
        if (file == null) {
            return "Content-Type: text/plain;charset=utf-8 ";
        }
        final var urlConnection = file.toURI().toURL().openConnection();
        final var mimeType = urlConnection.getContentType();
        return "Content-Type: " + mimeType + ";charset=utf-8 ";

    }

    private String buildResponseBody(final File file) throws IOException {
        if (file == null) {
            return "Hello world!";
        }
        return new String(Files.readAllBytes(file.toPath()));
    }


    private HttpResponse handleLogin(final HttpRequest request) throws IOException {
        if (request.isPost()) {
            return postLogin(request);
        }
        if (isAlreadyLoggedIn(request)) {
            return new HttpResponse("302 Found")
                    .addHeader("Location", "/index.html");
        }
        return handleStaticResponse("/login.html");
    }

    private HttpResponse postLogin(HttpRequest request) throws IOException {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var optionalUser = findUser(account, password);

        if (optionalUser.isEmpty()) {
            return buildStaticResponse("401 Unauthorized", "/401.html");
        }

        User user = optionalUser.get();
        log.info("user: {}", user);
        Session session = new Session(UUID.randomUUID().toString());
        sessionManager.add(session);
        session.addUser(user);
        return new HttpResponse("302 Found")
                .addHeader("Location", "/index.html")
                .setCookie("JSESSIONID", session.getId());
    }

    private Optional<User> findUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .stream().findFirst();
    }

    private boolean isAlreadyLoggedIn(final HttpRequest request) {
        String sessionId = request.getCookie("JSESSIONID");
        return sessionManager.findSession(sessionId) != null;
    }

    private HttpResponse postRegister(HttpRequest request) {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var email = form.get("email");
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return new HttpResponse("302 Found")
                .addHeader("Location", "/index.html");
    }

}
