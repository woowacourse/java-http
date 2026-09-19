package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String NOT_FOUND_FILE_PATH = "static/404.html";

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
             final var outputStream = connection.getOutputStream();
             final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String requestStartLine = bufferedReader.readLine();
            HttpRequest httpRequest = HttpRequest.from(requestStartLine);
            HttpResponse response = createResponse(httpRequest);
            response.writeTo(outputStream);

            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private HttpResponse createResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isRoot()) {
            return HttpResponse.ok("text/html", "Hello world!".getBytes(StandardCharsets.UTF_8));
        }
        return createResourceResponse(httpRequest);
    }

    private HttpResponse createResourceResponse(HttpRequest httpRequest) throws IOException {
        if (httpRequest.isLoginRequest() && httpRequest.hasParameters("account", "password")) {
            findUser(httpRequest);
        }
        String resourcePath = httpRequest.getResourcePath();
        if (httpRequest.isLoginRequest()) {
            resourcePath += ".html";
        }
        URL resource = getClass().getClassLoader().getResource(resourcePath);

        if (resource == null) {
            return createNotFoundResponse();
        }
        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        return HttpResponse.ok(getContentType(resource.getPath()), body);
    }

    private void findUser(HttpRequest httpRequest) {
        Optional<User> userOpt = InMemoryUserRepository.findByAccount(httpRequest.getParameter("account"));
        if (userOpt.isEmpty()) {
            return;
        }

        User user = userOpt.get();
        String password = httpRequest.getParameter("password");
        if (password != null && user.checkPassword(password)) {
            log.info(user.toString());
        }
    }

    private HttpResponse createNotFoundResponse() throws IOException {
        URL resource = getClass().getClassLoader().getResource(NOT_FOUND_FILE_PATH);

        if (resource == null) {
            return HttpResponse.notFound("text/plain", "404 NOT FOUND".getBytes(StandardCharsets.UTF_8));
        }

        byte[] body = Files.readAllBytes(new File(resource.getFile()).toPath());
        return HttpResponse.notFound(getContentType(resource.getPath()), body);
    }

    private String getContentType(String resource) {
        if (resource.endsWith(".html")) {
            return "text/html";
        }
        if (resource.endsWith(".css")) {
            return "text/css";
        }
        return "";
    }
}
