package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final String requestLine = reader.readLine();
            String[] parsedLine = requestLine.split(" ");
            String requestUri = parsedLine[1];

            URL resource;
            if (requestUri.endsWith(".html") || requestUri.endsWith(".css") || requestUri.endsWith(".js")) {
                resource = getClass().getClassLoader().getResource("static" + requestUri);
            } else {
                resource = getClass().getClassLoader().getResource(requestUri);
            }

            if (requestUri.contains("?")) {
                int index = requestUri.indexOf("?");
                String path = requestUri.substring(0, index);
                String queryString = requestUri.substring(index + 1);

                Map<String, String> params = parseQueryString(queryString);
                if ("/login".equals(path)) {
                    String account = params.get("account");
                    String password = params.get("password");

                    if (account == null || password == null) {
                        log.warn("Missing account or password");
                        return;
                    }

                    InMemoryUserRepository.findByAccount(account)
                            .ifPresentOrElse(user -> {
                                if (user.checkPassword(password)) {
                                    log.info("user : {}", user);
                                } else {
                                    log.info("Login failed for account: {}", account);
                                }
                            }, () -> log.info("No such account: {}", account));
                }

                resource = getClass().getClassLoader().getResource("static/" + path + ".html");
            }

            if (resource == null) {
                final var response = String.join("\r\n",
                        "HTTP/1.1 404 Not Found",
                        "Content-Length: 0",
                        "",
                        ""
                );
                outputStream.write(response.getBytes());
                outputStream.flush();
                return;
            }

            final var responseBody = new String(Files.readAllBytes(new File(resource.getFile()).toPath()));

            String contentType = "text/plain;charset=utf-8";
            if (requestUri.endsWith(".html")) {
                contentType = "text/html;charset=utf-8";
            } else if (requestUri.endsWith(".css")) {
                contentType = "text/css;charset=utf-8";
            } else if (requestUri.endsWith(".js")) {
                contentType = "application/javascript;charset=utf-8";
            } else if (requestUri.startsWith("/login")) {
                contentType = "text/html;charset=utf-8";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes().length,
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> params = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=", 2);
            if (keyValue.length == 2) {
                params.put(keyValue[0], keyValue[1]);
            }
        }
        return params;
    }
}
