package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
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
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
             final var outputStream = connection.getOutputStream()) {

            HttpRequestParser parser = new HttpRequestParser();
            HttpRequest request = parser.parse(bufferedReader);
            HttpResponse response = new HttpResponse(outputStream);

            if (request.isPath("/") && request.hasMethod(HttpRequestMethod.GET)) {
                final byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
                response.setBody(body);
                response.send();
                return;
            }

            if (request.isPath("/login") && request.hasMethod(HttpRequestMethod.GET)) {
                handleLogin(request, response);
                return;
            }

            if (request.isPath("/register") && request.hasMethod(HttpRequestMethod.POST)) {
                handleSignUp(request, response);
                return;
            }

            if (request.endsWith(".css") && request.hasMethod(HttpRequestMethod.GET)) {
                serveStaticFile(request, response, "text/css;charset=utf-8");
                response.send();
                return;
            }

            if (request.endsWith(".html") && request.hasMethod(HttpRequestMethod.GET)) {
                serveStaticFile(request, response, "text/html;charset=utf-8");
                response.send();
                return;
            }

            if (request.endsWith(".js") && request.hasMethod(HttpRequestMethod.GET)) {
                serveStaticFile(request, response, "text/javascript;charset=utf-8");
                response.send();
                return;
            }

            serveStaticFile(request, response, "text/html;charset=utf-8");
            response.send();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleLogin(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");

        if (account == null || password == null) {
            serveStaticFile(request, response, "text/html;charset=utf-8");
            response.send();
        }

        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
            log.info("user: {}", userOptional.get());
            response.sendRedirect(HttpResponseStatus.FOUND, "/index.html");
            return;
        }
        response.sendRedirect(HttpResponseStatus.FOUND, "/401.html");
    }

    private void handleSignUp(HttpRequest request, HttpResponse response) {

    }

    private void serveStaticFile(HttpRequest request, HttpResponse response, String contentType) throws IOException, URISyntaxException {
        final var resource = getClass().getClassLoader().getResource("static" + request.getPath());
        if (resource != null) {
            final Path resourcePath = Paths.get(resource.toURI());
            byte[] body = Files.readAllBytes(resourcePath);
            response.setContentType(contentType);
            response.setBody(body);
        }
    }
}
