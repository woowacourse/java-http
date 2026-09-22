package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
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
import java.util.UUID;

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
            HttpRequest httpRequest = HttpRequest.parse(inputStream);
            HttpResponse response = handleRequest(httpRequest);

            outputStream.write(response.toHttpMessage().getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private HttpResponse handleRequest(HttpRequest httpRequest) throws URISyntaxException, IOException {
        String requestTarget = httpRequest.getRequestTarget();

        if (requestTarget.equals("/")) {
            return HttpResponse.create("200 OK", "text/html", "Hello world!");
        }

        if (httpRequest.isPostMethod() && httpRequest.isPath("/login")) {
            return createLoginResponse(httpRequest);
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
        if (requestTarget.equals("/login") || requestTarget.equals("/register")) {
            resourcePath += ".html";
        }
        return resourcePath;
    }

    private String getContentType(String requestTarget){
        if(requestTarget.endsWith(".css")){
            return "text/css";
        }
        if(requestTarget.endsWith(".js")){
            return "text/javascript";
        }
        return "text/html";
    }

    private HttpResponse createLoginResponse(HttpRequest httpRequest) {
        if (!handleLogin(httpRequest)) {
            return HttpResponse.redirect("/401.html");
        }

        if (httpRequest.getCookie().contains("JSESSIONID")) {
            return HttpResponse.redirect("/index.html");
        }

        return HttpResponse.redirectWithCookie(
                "/index.html",
                "JSESSIONID=" + UUID.randomUUID()
        );
    }

    private boolean handleLogin(HttpRequest httpRequest) {
        Map<String, String> body = parseBody(httpRequest.getBody());
        String account = body.get("account");
        String password = body.get("password");

        if (account == null || password == null) {
            return false;
        }
        User user = InMemoryUserRepository.findByAccount(account).orElse(null);
        if (user != null && user.checkPassword(password)) {
            log.info(user.toString());
            return true;
        }
        return false;
    }

    private HttpResponse createRegisterResponse(HttpRequest httpRequest) {
        Map<String, String> body = parseBody(httpRequest.getBody());
        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

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
