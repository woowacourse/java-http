package org.apache.coyote.http11;

import static org.apache.coyote.http11.http.MediaType.DEFAULT_CHARSET;
import static org.apache.coyote.http11.http.MediaType.TEXT_CSS;
import static org.apache.coyote.http11.http.MediaType.TEXT_HTML;

import java.io.IOException;
import java.net.Socket;
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

            String body = mapResource(request);

            final var headers = new Headers();
            headers.put("Content-Type", contentType(request.getAccept()));
            headers.put("Content-Type", DEFAULT_CHARSET);
            headers.put("Content-Length", String.valueOf(body.getBytes().length));

            final var httpResponse = new HttpResponse(
                    request.getHttpVersion(),
                    HttpStatusCode.OK,
                    headers,
                    body
            );

            outputStream.write(httpResponse.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String mapResource(final HttpRequest request) throws IOException {
        String body = "";
        if (Objects.equals(request.getPath(), "/")) {
            body = "Hello world!";
        }
        if (Objects.equals(request.getPath(), "/index.html")) {
            var url = CLASS_LOADER.getResource("static" + request.getPath());
            body = Files.readString(Path.of(url.getPath()));
        }
        if (Objects.equals(request.getPath(), "/css/styles.css")) {
            var url = CLASS_LOADER.getResource("static" + request.getPath());
            body = Files.readString(Path.of(url.getPath()));
        }
        if (Objects.equals(request.getPath(), "/login")) {
            var url = CLASS_LOADER.getResource("static" + request.getPath() + ".html");
            body = Files.readString(Path.of(url.getPath()));
            String queryString = request.getQueryString();
            if (!queryString.isBlank()) {
                Map<String, String> queryParams = new ConcurrentHashMap<>();
                var params = queryString.split("&");
                for (String param : params) {
                    String[] keyValue = param.split("=");
                    queryParams.put(keyValue[0], keyValue[1]);
                }
                var account = queryParams.get("username");
                var user = InMemoryUserRepository.findByAccount(account).orElse(null);
                log.info("user: {}", user);
            }
        }
        return body;
    }

    private String contentType(final String accept) {
        if (accept != null && accept.contains(TEXT_CSS)) {
            return TEXT_CSS;
        }
        return TEXT_HTML;
    }
}
