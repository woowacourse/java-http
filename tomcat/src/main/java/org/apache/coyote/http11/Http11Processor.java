package org.apache.coyote.http11;

import com.techcourse.controller.RequestMapping;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.User;
import org.apache.catalina.controller.Controller;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final RequestMapping requestMapping = new RequestMapping();

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
            HttpRequest httpRequest = HttpRequest.parse(inputStream);
            HttpResponse response = handleRequest(httpRequest);

            outputStream.write(response.toHttpMessage().getBytes());
            outputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse handleRequest(HttpRequest httpRequest) throws Exception {
        String requestTarget = httpRequest.getRequestTarget();

        if (requestTarget.equals("/")) {
            return HttpResponse.create("200 OK", "text/html", "Hello world!");
        }

        if (httpRequest.isPath("/login")) {
            Controller controller = requestMapping.getController(requestTarget);
            return controller.service(httpRequest);
        }

        if (httpRequest.isPostMethod() && httpRequest.isPath("/register")) {
            return createRegisterResponse(httpRequest);
        }

        String resourcePath = getResourcePath(requestTarget);
        String contentType = getContentType(requestTarget);

        if (ClassLoader.getSystemResource(resourcePath) == null) {
            return HttpResponse.create("404 Not Found", "text/html", createResponseBody("static/404.html"));
        }

        return HttpResponse.create("200 OK", contentType, createResponseBody(resourcePath));
    }

    private String getResourcePath(String requestTarget) {
        String resourcePath = "static" + requestTarget;
        if (requestTarget.equals("/register")) {
            resourcePath += ".html";
        }
        return resourcePath;
    }

    private String getContentType(String requestTarget) {
        if (requestTarget.endsWith(".css")) {
            return "text/css";
        }
        if (requestTarget.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html";
    }

    private HttpResponse createRegisterResponse(HttpRequest httpRequest) {
        Map<String, String> body = parseBody(httpRequest.getBody());
        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

        if (account == null || account.isBlank()
                || password == null || password.isBlank()
                || email == null || email.isBlank()) {
            return HttpResponse.create("400 Bad Request", "text/plain", "Missing required fields");
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return HttpResponse.redirect("/index.html");
    }

    private Map<String, String> parseBody(String body) {
        Map<String, String> parameters = new HashMap<>();
        if (body.isBlank()) {
            return parameters;
        }

        String[] pairs = body.split("&");
        for (String pair : pairs) {
            String[] nameAndValue = pair.split("=", 2);
            String value = "";
            if (nameAndValue.length == 2) {
                value = nameAndValue[1];
            }
            String name = URLDecoder.decode(nameAndValue[0], StandardCharsets.UTF_8);
            parameters.put(name, URLDecoder.decode(value, StandardCharsets.UTF_8));
        }
        return parameters;
    }

    private String createResponseBody(String resourcePath) throws URISyntaxException, IOException {
        final Path path = Path.of(ClassLoader.getSystemResource(resourcePath).toURI());
        return Files.readString(path);
    }
}
