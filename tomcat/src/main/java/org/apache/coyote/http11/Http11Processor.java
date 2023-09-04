package org.apache.coyote.http11;

import java.io.File;
import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.Map;
import java.util.Optional;
import javax.annotation.Nullable;
import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final HttpRequestParser httpRequestParser;

    public Http11Processor(final Socket connection) {
        this.connection = connection;
        httpRequestParser = new HttpRequestParser();
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
            HttpRequest request = httpRequestParser.accept(inputStream);

            log.info("request uri: {}", request.getUri());
            final var response = handleRequest(request);

            outputStream.write(response.buildResponse().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse postLogin(HttpRequest request) throws IOException {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var user = findUser(account, password);
        if (user.isEmpty()) {
            File page = getFile("/401.html");
            String contentType = getContentType(page);
            String body = buildResponseBody(page);
            return new HttpResponse("401 Unauthorized", contentType, body);
        }
        log.info("user: {}", user.get());
        return new HttpResponse("302 Found", "Content-Type: text/plain;charset=utf-8 ", null,
                Map.of("Location", "/index.html"));
    }

    private HttpResponse postRegister(HttpRequest request) throws IOException {
        final var form = request.getForm();
        final var account = form.get("account");
        final var password = form.get("password");
        final var email = form.get("email");
        final var user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return new HttpResponse("302 Found", "Content-Type: text/plain;charset=utf-8 ", null,
                Map.of("Location", "/index.html"));
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


    private Optional<User> findUser(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .stream().findFirst();
    }

    private HttpResponse handleRequest(final HttpRequest request) throws IOException {
        final var uri = request.getUri();
        if (uri.equals("/")) {
            return getResource("/");
        }
        if (uri.equals("/login")) {
            if (request.isPost()) {
                return postLogin(request);
            }
            return getResource("/login.html");
        }
        if (uri.equals("/register")) {
            if (request.isPost()) {
                return postRegister(request);
            }
            return getResource("/register.html");
        }
        return getResource(uri);
    }

    private HttpResponse getResource(String uri) throws IOException {
        try {
            File page = getFile(uri);
            String contentType = getContentType(page);
            String body = buildResponseBody(page);
            return new HttpResponse("200 OK", contentType, body);
        } catch (Exception e) {
            File page = getFile("/404.html");
            String contentType = getContentType(page);
            String body = buildResponseBody(page);
            return new HttpResponse("404 Not Found", contentType, body);
        }
    }

    @Nullable
    private File getFile(final String uri) {
        if (uri.equals("/")) {
            return null;
        }
        final URL resource = getClass().getClassLoader().getResource("static" + uri);
        return Optional.ofNullable(resource.getFile()).map(File::new).get();
    }

}
