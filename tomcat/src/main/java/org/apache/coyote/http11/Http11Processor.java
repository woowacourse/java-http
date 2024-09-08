package org.apache.coyote.http11;

import static org.apache.coyote.http11.http.MediaType.DEFAULT_CHARSET;
import static org.apache.coyote.http11.http.MediaType.TEXT_CSS;
import static org.apache.coyote.http11.http.MediaType.TEXT_HTML;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.http.Headers;
import org.apache.coyote.http11.http.HttpRequest;
import org.apache.coyote.http11.http.HttpResponse;
import org.apache.coyote.http11.http.HttpStatusCode;
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
        try (final var inputStream = connection.getInputStream();
             final var outputStream = connection.getOutputStream()) {

            final var request = new HttpRequest(inputStream);
            final var httpResponse = mapResource(request);

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse mapResource(final HttpRequest request) throws IOException {
        final var defaultResourcePath = "static";
        URL url;
        var status = HttpStatusCode.OK;
        var body = "";
        if (Objects.equals(request.getPath(), "/")) {
            body = "Hello world!";
        }
        if (Objects.equals(request.getPath(), "/index.html")) {
            url = CLASS_LOADER.getResource(defaultResourcePath + "/index.html");
            body = Files.readString(Path.of(url.getPath()));
        }
        if (Objects.equals(request.getPath(), "/css/styles.css")) {
            url = CLASS_LOADER.getResource(defaultResourcePath + "/css/styles.css");
            body = Files.readString(Path.of(url.getPath()));
        }
        if (Objects.equals(request.getPath(), "/login")) {
            url = CLASS_LOADER.getResource(defaultResourcePath + "/login.html");
            body = Files.readString(Path.of(url.getPath()));
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
                    url = CLASS_LOADER.getResource(defaultResourcePath + "/401.html");
                    status = HttpStatusCode.UNAUTHORIZED;
                    body = Files.readString(Path.of(url.getPath()));
                } else {
                    status = HttpStatusCode.FOUND;
                    url = CLASS_LOADER.getResource(defaultResourcePath + "/index.html");
                    body = Files.readString(Path.of(url.getPath()));
                    log.info("user: {}", user);
                }
            }
        }

        final var headers = new Headers()
                .add("Content-Type", contentType(request.getAccept()))
                .add("Content-Length", contentLength(body));

        return HttpResponse.builder()
                .httpVersion(request.getHttpVersion())
                .httpStatusCode(status)
                .headers(headers)
                .body(body)
                .build();
    }

    private String contentType(final String accept) {
        String contentType = TEXT_HTML;
        if (accept != null && accept.contains(TEXT_CSS)) {
            contentType = TEXT_CSS;
        }
        return contentType + ";" + DEFAULT_CHARSET;
    }

    private String contentLength(final String body) {
        byte[] bodyBytes = body.getBytes();
        return String.valueOf(bodyBytes.length);
    }
}
