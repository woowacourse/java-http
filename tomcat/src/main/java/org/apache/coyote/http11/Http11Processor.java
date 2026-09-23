package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.IOException;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.UUID;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {
    private static final String STATIC_RESOURCE_PREFIX = "static";
    private static final String ROOT_RESPONSE_BODY = "Hello world!";
    private static final String LOGIN_PATH = "/login";
    private static final String LOGIN_RESOURCE_PATH = "/login.html";
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
            HttpRequest request = HttpRequest.readFrom(inputStream);
            if (request == null) {
                return;
            }
            HttpCookie cookies = new HttpCookie(request.findHeader("Cookie").orElse(""));
            RequestTarget requestTarget = request.getRequestTarget();
            String resourcePath = resolveResourcePath(requestTarget);
            byte[] responseBody = ROOT_RESPONSE_BODY.getBytes();
            if (!resourcePath.equals("/")) {
                String fileName = STATIC_RESOURCE_PREFIX + resourcePath;
                URL resource = getClass().getClassLoader().getResource(fileName);
                if (resource != null) {
                    Path path = Paths.get(resource.toURI());
                    responseBody = Files.readAllBytes(path);
                }
            }
            String contentType = contentTypeOf(requestTarget.getExtension());

            HttpResponse response;
            if (requestTarget.hasPath(LOGIN_PATH) && request.hasMethod("POST")) {
                String account = request.findFormParameter("account")
                        .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: account"));
                String password = request.findFormParameter("password")
                        .orElseThrow(() -> new IllegalArgumentException("필수 입력값 누락: password"));

                Optional<User> user = InMemoryUserRepository.findByAccount(account);
                user.ifPresent(value -> log.info("user : {}", value));

                boolean loginSuccess = user
                        .map(value -> value.checkPassword(password))
                        .orElse(false);

                String location = resolveLocation(loginSuccess);
                response = new HttpResponse("302 FOUND", contentType, responseBody)
                        .addHeader("Location", location);
            } else if (requestTarget.hasPath("/register") && request.hasMethod("POST")) {
                response = new HttpResponse("302 FOUND", contentType, responseBody)
                        .addHeader("Location", "/index.html");
            } else {
                response = new HttpResponse("200 OK", contentType, responseBody);
            }
            if (!cookies.hasJSessionId()) {
                response.addHeader("Set-Cookie", "JSESSIONID=" + UUID.randomUUID());
            }
            outputStream.write(response.toByteArray());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String resolveLocation(boolean loginSuccess) {
        if (loginSuccess) {
            return "/index.html";
        }
        return "/401.html";
    }

    private String resolveResourcePath(RequestTarget requestTarget) {
        if (requestTarget.hasPath(LOGIN_PATH)) {
            return LOGIN_RESOURCE_PATH;
        } else if (requestTarget.hasPath("/register")) {
            return "/register.html";
        }
        return requestTarget.getPath();
    }

    private String contentTypeOf(String extension) {
        if (extension.equals("css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
