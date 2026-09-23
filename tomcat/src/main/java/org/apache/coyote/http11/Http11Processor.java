package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT = "static";
    private static final String CONTENT_TYPE_HTML = "text/html";
    private static final String CONTENT_TYPE_CSS = "text/css";
    private static final String CONTENT_TYPE_JS = "application/javascript";

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

            BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));
            HttpRequest request = HttpRequest.parseFrom(br);

            HttpResponse response = createResponse(request);

            outputStream.write(response.convertString().getBytes());
            outputStream.flush();
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(HttpRequest request) throws IOException, URISyntaxException {
        if ("/".equals(request.getPath())) {
            return HttpResponse.ok(CONTENT_TYPE_HTML, "Hello world!");
        }

        String resourcePath = request.getPath();

        if ("/login".equals(resourcePath)) {
            resourcePath = "/login.html";

            if (!request.getQueries().isEmpty()) {
                if (login(request.getQueries())) {
                    return HttpResponse.found("/index.html");
                }

                return HttpResponse.found("/401.html");
            }
        }

        String responseBody = loadResponseBody(resourcePath);
        String contentType = findContentType(resourcePath);

        return HttpResponse.ok(contentType, responseBody);
    }

    private String loadResponseBody(String resourcePath) throws IOException, URISyntaxException {
        var resource = ClassLoader.getSystemResource(ROOT + resourcePath);
        Path path = Path.of(resource.toURI());
        return Files.readString(path);
    }

    private boolean login(Map<String, String> queries) {
        String account = queries.getOrDefault("account", "");
        String password = queries.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return false;
        }

        if (user.get().checkPassword(password)) {
            log.info("user : {}", user.toString());
            return true;
        }

        return false;
    }

    private String findContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return CONTENT_TYPE_CSS;
        }

        if (resourcePath.endsWith(".js")) {
            return CONTENT_TYPE_JS;
        }

        return CONTENT_TYPE_HTML;
    }
}
