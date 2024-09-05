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
import com.techcourse.model.User;
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

            final var response = generateResponse(reader);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String generateResponse(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        final var parts = requestLine.split(" ");
        final var method = parts[0];
        final var uri = parts[1];

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
            final var queryString = uri.substring(uri.indexOf('?') + 1);
            final var queryParams = parseQueryParam(queryString);
            if (isAuthenticateUser(queryParams)) {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /index.html ",
                        "Content-Length: 0 ",
                        "Connection: close ",
                        "");
            } else {
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: /401.html ",
                        "Content-Length: 0 ",
                        "Connection: close ",
                        "");
            }
        }
        if (method.equals("POST") && uri.contains("register")) {
            final var responseBody = getResponseBody(reader);
            final var newUser = new User(responseBody.get("account"), responseBody.get("password"), responseBody.get("email"));
            InMemoryUserRepository.save(newUser);
            return String.join("\r\n",
                    "HTTP/1.1 302 Found ",
                    "Location: /index.html ",
                    "Content-Length: 0 ",
                    "Connection: close ",
                    "");
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

    private Map<String, String> getResponseBody(BufferedReader reader) throws IOException {
        final var requestHeader = parseRequestHeader(reader);
        int contentLength = Integer.parseInt(requestHeader.get("Content-Length"));
        char[] buffer = new char[contentLength];
        reader.read(buffer, 0, contentLength);
        final var requestBody = new String(buffer);
        return parseQueryParam(requestBody);
    }

    private Map<String, String> parseRequestHeader(final BufferedReader reader) throws IOException {
        final Map<String, String> result = new HashMap<>();
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            String[] parts = line.split(": ", 2);
            result.put(parts[0], parts[1]);
        }
        return result;
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
