package org.apache.coyote.http11;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.apache.coyote.Processor;
import org.apache.coyote.http11.common.ContentType;
import org.apache.coyote.http11.common.SessionManager;
import org.apache.coyote.http11.request.HttpMethod;
import org.apache.coyote.http11.request.HttpRequest;
import org.apache.coyote.http11.response.HttpResponse;
import org.apache.coyote.http11.response.HttpStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.techcourse.exception.UnauthorizedException;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.service.UserService;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final SessionManager SESSION_MANAGER = new SessionManager();

    private final UserService userService = new UserService(SESSION_MANAGER);
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
            HttpRequest request = new HttpRequest(inputStream);
            HttpResponse response = new HttpResponse();

            try {
                if (request.getPath().get().equals("/login")) {
                    if (request.getMethod() == HttpMethod.GET) {
                        if (userService.getLoggedUser(request.getCookies()) == null) {
                            request.setPath("/login.html");
                        } else {
                            request.setPath("/index.html");
                        }
                    }
                    if (request.getMethod() == HttpMethod.POST) {
                        userService.login(request, response);
                        response.setHttpStatus(HttpStatus.FOUND);
                        response.getHeaders().put("Location", "/index.html");
                    }
                }

                if (request.getPath().get().equals("/register")) {
                    if (request.getMethod() == HttpMethod.GET) {
                        request.setPath("/register.html");
                    }
                    if (request.getMethod() == HttpMethod.POST) {
                        userService.register(request);
                        response.setHttpStatus(HttpStatus.FOUND);
                        response.getHeaders().put("Location", "/index.html");
                    }
                }
                response.setContentType(ContentType.fromPath(request.getPath()));
            } catch (UnauthorizedException e) {
                request.setPath("/401.html");
                response.setContentType(ContentType.HTML);
                response.setHttpStatus(HttpStatus.UNAUTHORIZED);
                response.getHeaders().clear();
            } catch (IllegalArgumentException e) {
                request.setPath("/404.html");
                response.setContentType(ContentType.HTML);
                response.setHttpStatus(HttpStatus.NOT_FOUND);
                response.getHeaders().clear();
            }

            if (response.isStaticPage()) {
                response.setResponseBody(getStaticPage(request.getPath().get()));
            }
            final var output = response.buildResponse();
            outputStream.write(output.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getStaticPage(String requestPath) throws IOException, URISyntaxException {
        String normalizedPath = Paths.get(requestPath).normalize().toString();
        if (normalizedPath.contains("..")) {
            throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
        }
        if (normalizedPath.equals("/") || normalizedPath.equals("\\")) {
            return "Hello world!";
        }
        try (InputStream inputStream = getClass().getClassLoader().getResourceAsStream("static" + normalizedPath)) {
            if (inputStream == null) {
                throw new IllegalArgumentException("존재하지 않는 페이지입니다.");
            }
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }
}
