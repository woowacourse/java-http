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
import org.apache.coyote.request.HttpRequest;
import org.apache.coyote.request.HttpRequestParser;
import org.apache.coyote.response.HttpContentTypeResolver;
import org.apache.coyote.response.HttpResponse;
import org.apache.coyote.response.StaticResourcePathGenerator;
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
                respond(HttpResponse.of("HTTP/1.1 404 Not Found", "static/404.html"), outputStream);
                return;
            }
            if (!request.queries().isEmpty() && handleApiIfNeeded(request, outputStream)) {
                return;
            }
            String resourcePath = StaticResourcePathGenerator.generate(request.path());
            if (resourcePath != null) {
                byte[] resourceBody = readPathFile(resourcePath);
                if (resourceBody != null) {
                    respond(HttpResponse.of(
                            "HTTP/1.1 200 OK",
                            HttpContentTypeResolver.resolve(resourcePath),
                            resourceBody
                    ), outputStream);
                    return;
                }
            }
            if (request.queries().isEmpty() && handleApiIfNeeded(request, outputStream)) {
                return;
            }
            respond(HttpResponse.of("HTTP/1.1 404 Not Found", "static/404.html"), outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private boolean handleApiIfNeeded(HttpRequest request, OutputStream outputStream) throws IOException {
        if ("/login".equals(request.path())) {
            HttpResponse loginResponse = processLoginMemberInfo(request);
            System.out.println(loginResponse.createHeader());
            respond(loginResponse, outputStream);
            return true;
        }
        return false;
    }

    private HttpResponse processLoginMemberInfo(HttpRequest httpRequest) {
        String account = httpRequest.getQueryValue("account")
                .orElse(null);
        String password = httpRequest.getQueryValue("password")
                .orElse(null);
        if (account == null || password == null) {
            return HttpResponse.redirect("401.html");
        }
        Optional<User> user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return HttpResponse.redirect("401.html");
        }
        log.info("User: {}", user.get());
        return HttpResponse.redirect("/index.html");
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
