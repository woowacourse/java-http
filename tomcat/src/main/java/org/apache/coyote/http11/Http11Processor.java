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
            String response = handleRequest(httpRequest);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String handleRequest(HttpRequest httpRequest) throws URISyntaxException, IOException {
        String requestTarget = httpRequest.getRequestTarget();

        if (requestTarget.equals("/")) {
            return createResponse("Hello world!", "text/html", "200 OK");
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
            return createResponse(createResponseBody("static/404.html"), "text/html", "404 Not Found");
        }

        return createResponse(createResponseBody(resourcePath), contentType, "200 OK");
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

    private String createLoginResponse(HttpRequest httpRequest) {
        if (handleLogin(httpRequest)) {
            return createRedirectResponse("/index.html");
        }
        return createRedirectResponse("/401.html");
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

    private String createRegisterResponse(HttpRequest httpRequest) {
        Map<String, String> body = parseBody(httpRequest.getBody());
        String account = body.get("account");
        String password = body.get("password");
        String email = body.get("email");

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
        return createRedirectResponse("/index.html");
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

    private String createRedirectResponse(String location) {
        return String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                ""
        );
    }

    private String createResponse(String responseBody, String contentType, String status) {
        String contentTypeHeader = "Content-Type: " + contentType;
        if (contentType.startsWith("text/")) {
            contentTypeHeader += ";charset=utf-8";
        }
        return String.join("\r\n",
                "HTTP/1.1 " + status,
                contentTypeHeader,
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String createResponseBody(String resourcePath) throws URISyntaxException, IOException {
        final Path path = Path.of(ClassLoader.getSystemResource(resourcePath).toURI());
        return Files.readString(path);
    }
}
