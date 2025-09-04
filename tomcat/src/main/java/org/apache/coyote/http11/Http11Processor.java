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

            HttpRequest request = new HttpRequest(bufferedReader);
            HttpResponse response = new HttpResponse(outputStream);
            final String path = request.getPath();

            if ("/".equals(path)) {
                final byte[] body = "Hello world!".getBytes(StandardCharsets.UTF_8);
                response.setBody(body);
                response.send();
                return;
            }

            if ("/login".equals(path)) {
                handleLogin(request, response);
                response.send();
                return;
            }

            if (path.endsWith(".css")) {
                serveStaticFile(path, response, "text/css;charset=utf-8");
                response.send();
                return;
            }

            if (path.endsWith(".js")) {
                serveStaticFile(path, response, "text/javascript;charset=utf-8");
                response.send();
                return;
            }

            serveStaticFile("/index.html", response, "text/html;charset=utf-8");
            response.send();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleLogin(HttpRequest request, HttpResponse response) throws IOException, URISyntaxException {
        String account = request.getQueryParam("account");
        String password = request.getQueryParam("password");

        Optional<User> userOptional = InMemoryUserRepository.findByAccount(account);
        if (userOptional.isPresent() && userOptional.get().checkPassword(password)) {
            log.info("user: {}", userOptional.get());
        }

        serveStaticFile("/login.html", response, "text/html;charset=utf-8");
    }

    private void serveStaticFile(String path, HttpResponse response, String contentType) throws IOException, URISyntaxException {
        final var resource = getClass().getClassLoader().getResource("static" + path);
        if (resource != null) {
            final Path resourcePath = Paths.get(resource.toURI());
            byte[] body = Files.readAllBytes(resourcePath);
            response.setContentType(contentType);
            response.setBody(body);
        }
    }
}
