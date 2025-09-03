package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
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
            final var reader = new BufferedReader(new InputStreamReader(inputStream));

            final var requestLine = reader.readLine();
            if (requestLine == null || requestLine.isBlank()) {
                return;
            }

            final var parts = requestLine.split(" ");
            final var method = parts[0];
            final var uri = parts[1];

            String path = uri;
            String queryString = null;
            int index = uri.indexOf("?");
            if (index != -1) {
                path = uri.substring(0, index);
                queryString = uri.substring(index + 1);
            }

            Map<String, String> params = new HashMap<>();
            if (queryString != null) {
                final var queries = queryString.split("&");

                for (String query : queries) {
                    final var keyValue = query.split("=");
                    params.put(keyValue[0], keyValue[1]);
                }
            }

            final byte[] responseBody;
            final String contentType;

            if ("/login".equals(path)) {
                final var resourceUrl = getClass().getClassLoader()
                        .getResource("static/login.html");
                responseBody = Files.readAllBytes(Path.of(resourceUrl.toURI()));
                contentType = "text/html;charset=utf-8 ";

                final String account = params.get("account");
                final String password = params.get("password");
                InMemoryUserRepository.findByAccount(account)
                        .ifPresent(user -> {
                                    if (user.checkPassword(password)) {
                                        log.info("user: {}", user);
                                    }
                                }
                        );

            } else {
                final var resourcePath = "static" + path;
                final var resourceUrl = getClass().getClassLoader()
                        .getResource(resourcePath);

                if (resourceUrl != null) {
                    responseBody = Files.readAllBytes(Path.of(resourceUrl.toURI()));
                    contentType = getContentType(uri);
                } else {
                    responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
                    contentType = "text/plain;charset=utf-8 ";
                }
            }


            final var responseHeaders = String.join(
                    "\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType,
                    "Content-Length: " + responseBody.length + " ",
                    ""
            );

            outputStream.write((responseHeaders + "\r\n").getBytes());
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getContentType(final String path) {
        if (path.endsWith(".html")) {
            return "text/html;charset=utf-8 ";
        }
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }
        return "text/plain;charset=utf-8 ";
    }
}
