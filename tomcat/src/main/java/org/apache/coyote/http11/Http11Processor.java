package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String RESOURCES_PREFIX = "static";

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line = reader.readLine();
            HttpRequest request = HttpRequest.from(line);

            String path = request.path();
            final var response = getResponse(request);

            outputStream.write(response.toBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse getResponse(final HttpRequest request) {
        String path = request.path();
        if (!path.equals("/") && isResourcePresent(path)) {
            return new HttpResponse(HttpStatus.OK, getContentType(path), modelToView(path));
        }

        if (path.equals("/") && request.method().equals("GET")) {
            return new HttpResponse(HttpStatus.OK, getContentType(path), "Hello world");
        }
        if (path.equals("/login") && request.method().equals("GET")) {
            String body = (modelToView("/login.html"));
            if (!request.hasParameters()) {
                return new HttpResponse(HttpStatus.OK, getContentType(path), body);
            }
            String account = request.getParameter("account");
            String password = request.getParameter("password");

            if (isLoginSuccess(account, password)) {
                return new HttpResponse(HttpStatus.FOUND, getContentType(path), body, URI.create("/index.html"));
            }
            return new HttpResponse(HttpStatus.FOUND, getContentType(path), body, URI.create("/401.html"));
        }

        return new HttpResponse(HttpStatus.BAD_REQUEST, getContentType(path), "Bad Request");
    }

    private boolean isLoginSuccess(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .isPresent();
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html; charset=utf-8";
    }

    private boolean isResourcePresent(String path) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + path);
        return resource != null;
    }

    private String modelToView(String path) {
        final URL resource = getClass().getClassLoader().getResource(RESOURCES_PREFIX + path);
        if (resource == null) {
            log.info("존재하지 않는 파일 명입니다. 파일 경로를 확인해주세요. path: {}", path);
            return "";
        }
        try {
            URI uri = resource.toURI();
            return Files.readString(Paths.get(uri));
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
        return "";
    }
}
