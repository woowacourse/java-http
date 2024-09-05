package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.cookie.HttpCookie;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
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
        final var requestHeader = parseRequestHeader(reader);

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
        if (method.equals("GET") && uri.equals("/login")) {
            final var cookie = new HttpCookie(requestHeader.getOrDefault("Cookie", "Cookie"));
            if (cookie.hasJSESSIONID()) {
                final var session = SessionManager.getInstance().findSession(cookie.getJSESSIONID());
                final var user = (User) session.getAttribute("user");
                if (InMemoryUserRepository.findByAccount(user.getAccount()).isPresent()) {
                    return String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /index.html ",
                            "Content-Length: 0 ",
                            "Connection: close ",
                            "");
                }
            }
        }
        if (method.equals("POST") && uri.equals("/login")) {
            final var responseBody = getResponseBody(reader, Integer.parseInt(requestHeader.get("Content-Length")));
            if (isAuthenticateUser(responseBody)) {
                HttpCookie cookie = new HttpCookie(requestHeader.getOrDefault("Cookie", "Cookie"));
                if (cookie.hasJSESSIONID()) {
                    return String.join("\r\n",
                            "HTTP/1.1 302 Found ",
                            "Location: /index.html ",
                            "Content-Length: 0 ",
                            "Connection: close ",
                            "");
                }

                final var session = new Session(UUID.randomUUID().toString());
                InMemoryUserRepository.findByAccount(responseBody.get("account"))
                        .ifPresent(user -> session.setAttribute("user", user));
                return String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Set-Cookie: JSESSIONID=" + session.getId() + " ",
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
            final var responseBody = getResponseBody(reader, Integer.parseInt(requestHeader.get("Content-Length")));
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

    private Map<String, String> getResponseBody(final BufferedReader reader, final int contentLength) throws IOException {
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
