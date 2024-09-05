package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
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
            final var reader = new BufferedReader(new InputStreamReader(inputStream));
            final var requestHeader = reader.readLine();
            final var parts = requestHeader.split(" ");
            final var method = parts[0];
            final var uri = parts[1];

            final var response = generateResponse(method, uri);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(String method, String uri) throws IOException {
        if (method.equals("GET") && uri.equals("/")) {
            final var responseBody = "Hello world!";

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: text/html;charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }
        if (method.equals("GET") && ContentMimeType.isEndsWithExtension(uri)) {
            final var resource = getClass().getClassLoader().getResource("static" + uri);
            final var fileContent = Files.readAllBytes(new File(resource.getFile()).toPath());
            final var responseBody = new String(fileContent);
            final var extension = uri.substring(uri.lastIndexOf('.') + 1);

            return String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + ContentMimeType.getMimeByExtension(extension) + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);
        }
        if (method.equals("GET") && uri.contains("login") && hasQueryParam(uri)) {
            final var resource = getClass().getClassLoader().getResource("static/index.html");
            final var fileContent = Files.readAllBytes(new File(resource.getFile()).toPath());
            final var responseBody = new String(fileContent);

            final var queryString = uri.substring(uri.indexOf('?') + 1);
            final var queryParams = parseQueryParam(queryString);
            if (isAuthenticateUser(queryParams)) {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html ",
                        "Content-Length: 0 ",
                        "Connection: close ",
                        "",
                        responseBody);
            } else {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /401.html ",
                        "Content-Length: 0 ",
                        "Connection: close ",
                        "",
                        responseBody);
            }
        }
        final var resource = getClass().getClassLoader().getResource("static" + uri + ".html");
        final var fileContent = Files.readAllBytes(new File(resource.getFile()).toPath());
        final var responseBody = new String(fileContent);
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + ContentMimeType.getMimeByExtension("html") + ";charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private boolean hasQueryParam(final String uri) {
        return uri.contains("?");
    }

    private Map<String, String> parseQueryParam(final String queryString) {
        Map<String, String> result = new HashMap<>();
        String[] pairs = queryString.split("&");
        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            result.put(keyValue[0], keyValue[1]);
        }
        return result;
    }

    private boolean isAuthenticateUser(final Map<String, String> queryParams) {
        return InMemoryUserRepository.findByAccount(queryParams.get("account"))
                .filter(user -> user.checkPassword(queryParams.get("password")))
                .map(user -> {
                    log.info("user : {}", user);
                    return true;
                })
                .orElseGet(() -> false);
    }
}
