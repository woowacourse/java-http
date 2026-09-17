package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

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

            HttpRequest requestTarget = getRequestTarget(inputStream);
            String requestPath = requestTarget.getHttpPath();

            if ("/login".equals(requestPath)) {
                logLoginUser(requestTarget);
            }

            String responseBody = getResponseBody(requestPath);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + getContentType(requestPath) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return "Hello world!";
        }

        File file = getPageFile(requestPath);
        return Files.readString(file.toPath(), StandardCharsets.UTF_8);
    }

    private HttpRequest getRequestTarget(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

        String requestLine = reader.readLine();
        if (requestLine == null) {
            throw new IOException("HTTP 요청 라인이 존재하지 않습니다.");
        }

        return HttpRequest.from(requestLine);
    }

    private void logLoginUser(HttpRequest httpRequest) {
        String account = httpRequest.getParams("account");
        String password = httpRequest.getParams("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("login user: {}", user));
    }

    private File getPageFile(String path) throws FileNotFoundException {
        ClassLoader classLoader = getClass().getClassLoader();

        URL resource = classLoader.getResource("static" + path);

        if (resource == null) {
            resource = classLoader.getResource("static" + path + ".html");
        }

        if (resource == null) {
            throw new FileNotFoundException(path);
        }

        try {
            return new File(resource.toURI());
        } catch (URISyntaxException e) {
            throw new UncheckedServletException(e);
        }
    }

    private String getContentType(String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html";
    }
}
