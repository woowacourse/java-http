package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

import static java.nio.charset.StandardCharsets.UTF_8;

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
             final var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8))) {

            final Optional<HttpRequest> request = HttpRequest.from(reader);
            if (request.isEmpty()) {
                return;
            }
            final HttpResponse response = createResponse(request.get());

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException | HttpRequestParseException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(final HttpRequest request) throws IOException, URISyntaxException {
        final String path = request.getPath();

        if (path.equals("/")) {
            return new HttpResponse(HttpStatus.OK, "text/html", "Hello world!");
        }

        if (path.equals("/login")) {
            login(request);
            final var loginPage = findResource("/login.html");
            return new HttpResponse(HttpStatus.OK, "text/html", readResource(loginPage));
        }

        final var resource = findResource(path);
        if (resource == null) {
            final var notFound = findResource("/404.html");
            return new HttpResponse(HttpStatus.NOT_FOUND, "text/html", readResource(notFound));
        }

        return new HttpResponse(HttpStatus.OK, contentType(path), readResource(resource));
    }

    private void login(final HttpRequest request) {
        final String account = request.getQueryParameter("account");
        final String password = request.getQueryParameter("password");
        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("user : {}", user));
    }

    private String readResource(final URL resource) throws IOException, URISyntaxException {
        return Files.readString(Path.of(resource.toURI()), UTF_8);
    }

    private String contentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }

    private URL findResource(final String path) {
        return getClass().getClassLoader().getResource("static" + path);
    }
}
