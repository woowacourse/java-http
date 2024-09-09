package org.apache.coyote.http11;

import static org.apache.coyote.http11.http.MediaType.DEFAULT_CHARSET;
import static org.apache.coyote.http11.http.MediaType.TEXT_CSS;
import static org.apache.coyote.http11.http.MediaType.TEXT_HTML;
import static org.apache.coyote.http11.http.MediaType.TEXT_JAVASCRIPT;

import java.io.IOException;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatusCode;
import org.apache.coyote.http11.http.NoHandlerFoundException;
import org.apache.coyote.http11.http.NoResourceFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final ClassLoader CLASS_LOADER = ClassLoader.getSystemClassLoader();

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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream()
        ) {
            final var request = new HttpRequest(inputStream);
            final var response = handle(request);
            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handle(final HttpRequest request) throws IOException {
        if (isStaticPath(request)) {
            return handleStaticResource(request);
        }
        if (Objects.equals(request.getPath(), "/")) {
            return handleRoot(request);
        }
        if (Objects.equals(request.getPath(), "/login")) {
            return handleLogin(request);
        }
        throw new NoHandlerFoundException(request.getPath());
    }

    private boolean isStaticPath(final HttpRequest request) {
        final var staticExtensions = List.of(".html", ".css", ".js");
        final var path = request.getPath();
        return staticExtensions.stream().anyMatch(path::endsWith);
    }

    private HttpResponse handleStaticResource(final HttpRequest request) throws IOException {
        var path = resourcePath(request.getPath());
        final var body = Files.readString(path);
        return HttpResponse.builder()
                .httpVersion(request.getHttpVersion())
                .httpStatusCode(HttpStatusCode.OK)
                .contentType(contentType(request))
                .contentLength(body)
                .body(body)
                .build();
    }

    private HttpResponse handleRoot(final HttpRequest request) {
        var status = HttpStatusCode.OK;
        var body = "Hello world!";
        return HttpResponse.builder()
                .httpVersion(request.getHttpVersion())
                .httpStatusCode(status)
                .contentType(contentType(request))
                .contentLength(body)
                .body(body)
                .build();
    }

    private HttpResponse handleLogin(final HttpRequest request) throws IOException {
        var status = HttpStatusCode.OK;
        var path = resourcePath("/login.html");
        var body = Files.readString(path);
        String queryString = request.getQueryString();
        if (!queryString.isBlank()) {
            Map<String, String> queryParams = new ConcurrentHashMap<>();
            var params = queryString.split("&");
            for (String param : params) {
                String[] keyValue = param.split("=");
                queryParams.put(keyValue[0], keyValue[1]);
            }
            var account = queryParams.get("account");
            var password = queryParams.get("password");
            var user = InMemoryUserRepository.findByAccount(account).orElse(null);
            if (user == null || !user.checkPassword(password)) {
                status = HttpStatusCode.UNAUTHORIZED;
                path = resourcePath("/401.html");
                body = Files.readString(path);
            } else {
                status = HttpStatusCode.FOUND;
                path = resourcePath("/index.html");
                body = Files.readString(path);
                log.info("user: {}", user);
            }
        }
        return HttpResponse.builder()
                .httpVersion(request.getHttpVersion())
                .httpStatusCode(status)
                .contentType(contentType(request))
                .contentLength(body)
                .body(body)
                .build();
    }

    private Path resourcePath(final String path) {
        final var url = CLASS_LOADER.getResource("static" + path);
        if (url == null) {
            throw new NoResourceFoundException(path);
        }
        return Path.of(url.getPath());
    }

    private String contentType(final HttpRequest request) {
        if (request.getPath().endsWith(".html") || request.getAccept().contains(TEXT_HTML)) {
            return TEXT_HTML + ";" + DEFAULT_CHARSET;
        }

        if (request.getPath().endsWith(".css") || request.getAccept().contains(TEXT_CSS)) {
            return TEXT_CSS + ";" + DEFAULT_CHARSET;
        }

        if (request.getPath().endsWith(".js") || request.getAccept().contains(TEXT_JAVASCRIPT)) {
            return TEXT_JAVASCRIPT + ";" + DEFAULT_CHARSET;
        }
        return null;
    }
}
