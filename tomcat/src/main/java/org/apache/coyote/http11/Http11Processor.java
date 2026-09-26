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

    private static boolean hasBlankParameter(Map<String, String> parameters, String... requiredKeys) {
        for (String key : requiredKeys) {
            String value = parameters.get(key);
            if (value == null || value.isBlank()) {
                return true;
            }
        }

        return false;
    }

    private static boolean isFormUrlEncoded(HttpRequest request) {
        return request.getHeaders().getOrDefault("Content-Type", "").equals("application/x-www-form-urlencoded");
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
        String resourcePath = request.getPath();

        if ("/".equals(resourcePath)) {
            return HttpResponse.ok(CONTENT_TYPE_HTML, "Hello world!");
        }

        if ("/login".equals(resourcePath) && request.getMethod().equals("GET")) {
            Session session = request.getSession(false);
            if (session != null && session.getAttribute("user") != null) {
                return HttpResponse.found("/index.html");
            }

            return HttpResponse.ok(CONTENT_TYPE_HTML, loadResponseBody("/login.html"));
        }

        if ("/login".equals(resourcePath) && request.getMethod().equals("POST")) {
            if (!isFormUrlEncoded(request)) {
                return HttpResponse.unsupportedMediaType();
            }

            Map<String, String> userInfo = request.getFormParameters();

            if (hasBlankParameter(userInfo, "account", "password")) {
                return HttpResponse.badRequest();
            }

            Optional<User> user = login(userInfo);
            if (user.isPresent()) {
                Session session = request.getSession(true);
                session.setAttribute("user", user.get());

                HttpResponse response = HttpResponse.found("/index.html");
                response.addCookie(HttpCookie.ofJSessionId(session.getId()));
                return response;
            }

            return HttpResponse.found("/401.html");
        }

        if ("/register".equals(resourcePath) && request.getMethod().equals("GET")) {
            resourcePath = "/register.html";
            return HttpResponse.ok(CONTENT_TYPE_HTML, loadResponseBody(resourcePath));
        }

        if ("/register".equals(resourcePath) && request.getMethod().equals("POST")) {
            if (!isFormUrlEncoded(request)) {
                return HttpResponse.unsupportedMediaType();
            }

            Map<String, String> userInfo = request.getFormParameters();

            if (hasBlankParameter(userInfo, "account", "password", "email")) {
                return HttpResponse.badRequest();
            }

            String account = userInfo.get("account");
            String password = userInfo.get("password");
            String email = userInfo.get("email");

            User user = new User(account, password, email);
            InMemoryUserRepository.save(user);

            return HttpResponse.found("/index.html");
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

    private Optional<User> login(Map<String, String> parameters) {
        String account = parameters.getOrDefault("account", "");
        String password = parameters.getOrDefault("password", "");

        Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return Optional.empty();
        }

        if (user.get().checkPassword(password)) {
            log.info("user : {}", user.get());
            return user;
        }

        return Optional.empty();
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
