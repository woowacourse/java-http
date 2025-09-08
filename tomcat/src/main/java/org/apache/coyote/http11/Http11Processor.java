package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.util.HttpContentTypeResolver;
import org.apache.coyote.util.HttpRequest;
import org.apache.coyote.util.HttpRequestParser;
import org.apache.coyote.util.HttpResponse;
import org.apache.coyote.util.StaticResourcePathGenerator;
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
             final var outputStream = connection.getOutputStream()) {

            HttpRequest request = HttpRequestParser.parse(inputStream);
            if (request == null) {
                respond(HttpResponse.of(
                        "HTTP/1.1 404 Not Found",
                        "static/404.html"
                ), outputStream);
                return;
            }
            String path = StaticResourcePathGenerator.generate(request.path());
            if (path == null) {
                respond(HttpResponse.of(
                        "HTTP/1.1 200 OK",
                        "text/html;charset=utf-8",
                        "Hello world!".getBytes()
                ), outputStream);
                return;
            }
            byte[] body = readPathFile(path);
            if (body == null) {
                respond(HttpResponse.of(
                        "HTTP/1.1 404 Not Found",
                        "static/404.html"
                ), outputStream);
                return;
            }
            if ("/login".equals(request.path())) {
                processLoginMemberInfo(request);
            }
            respond(HttpResponse.of(
                    "HTTP/1.1 200 OK",
                    HttpContentTypeResolver.resolve(path),
                    body
            ), outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void processLoginMemberInfo(HttpRequest httpRequest) {
        String account = httpRequest.getQueryValue("account")
                .orElse(null);
        String password = httpRequest.getQueryValue("password")
                .orElse(null);
        if (account == null || password == null) {
            return;
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty()) {
            return;
        }
        if (user.get().checkPassword(password)) {
            log.info("User: {}", user.get());
        }
    }

    private byte[] readPathFile(String requestPath) {
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream(requestPath)) {
            if (inputStream == null) {
                return null;
            }
            return inputStream.readAllBytes();
        } catch (IOException e) {
            return null;
        }
    }

    private void respond(HttpResponse httpResponse, OutputStream outputStream) throws IOException {
        outputStream.write(httpResponse.createHeader().getBytes());
        outputStream.write(httpResponse.getBody());
        outputStream.flush();
    }
}
