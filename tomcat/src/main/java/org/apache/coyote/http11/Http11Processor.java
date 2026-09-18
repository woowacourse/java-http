package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String uri = requestLine.split(" ")[1];

            String line;
            while (true) {
                line = reader.readLine();
                if (line == null) {
                    return;
                }
                if (line.isEmpty()) {
                    break;
                }
            }

            final int index = uri.indexOf("?");
            String path = uri;
            String queryString = "";

            if (index >= 0) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }

            if ("/login".equals(path) && !queryString.isEmpty()) {
                final String[] parameters = queryString.split("&");
                final String account = parameters[0].split("=", 2)[1];
                final String password = parameters[1].split("=", 2)[1];

                InMemoryUserRepository.findByAccount(account)
                        .filter(user -> user.checkPassword(password))
                        .ifPresent(user -> log.info("login user: {}", user.getAccount()));
            }

            String responseBody = "Hello world!";
            if (!path.equals("/")) {
                final String resourcePath = "/login".equals(path) ? "/login.html" : path;

                try (final InputStream resourceStream = getClass()
                        .getClassLoader()
                        .getResourceAsStream("static" + resourcePath)) {

                    if (resourceStream == null) {
                        throw new IllegalArgumentException("리소스를 찾을 수 없습니다: " + "static" + resourcePath);
                    }
                    responseBody = new String(resourceStream.readAllBytes(), StandardCharsets.UTF_8);
                }
            }

            final String contentType = getContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        if (path.endsWith(".js")) {
            return "application/javascript;charset=utf-8 ";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml;charset=utf-8 ";
        }
        return "text/html;charset=utf-8 ";
    }
}
