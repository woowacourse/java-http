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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }
            final String uri = requestLine.split(" ")[1];

            final String response = createResponse(uri);

            outputStream.write(response.getBytes(UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String createResponse(final String uri) throws IOException, URISyntaxException {
        final int index = uri.indexOf("?");
        final String path;
        final String queryString;
        if (index == -1) {
            path = uri;
            queryString = "";
        } else {
            path = uri.substring(0, index);
            queryString = uri.substring(index + 1);
        }

        if (path.equals("/")) {
            return response("HTTP/1.1 200 OK ", "text/html", "Hello world!");
        }

        if (path.equals("/login")) {
            login(parseQueryString(queryString));
            final var loginPage = findResource("/login.html");
            return response("HTTP/1.1 200 OK ", "text/html", readResource(loginPage));
        }

        final var resource = findResource(path);
        if (resource == null) {
            final var notFound = findResource("/404.html");
            return response("HTTP/1.1 404 Not Found ", "text/html", readResource(notFound));
        }

        return response("HTTP/1.1 200 OK ", contentType(path), readResource(resource));
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> params = new HashMap<>();
        if (queryString.isEmpty()) {
            return params;
        }
        for (final String pair : queryString.split("&")) {
            final String[] keyAndValue = pair.split("=", 2);
            if (keyAndValue.length == 2) {
                params.put(keyAndValue[0], keyAndValue[1]);
            }
        }
        return params;
    }

    private void login(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");
        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("user : {}", user));
    }

    private String readResource(final URL resource) throws IOException, URISyntaxException {
        return Files.readString(Path.of(resource.toURI()), UTF_8);
    }

    private String response(final String statusLine, final String contentType, final String body) {
        return String.join("\r\n",
                statusLine,
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes(UTF_8).length + " ",
                "",
                body);
    }

    private String contentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        if (path.endsWith(".js")) {
            return "application/javascript";
        }
        if (path.endsWith(".svg")) {
            return "image/svg+xml";
        }
        return "text/html";
    }

    private URL findResource(final String path) {
        return getClass().getClassLoader().getResource("static" + path);
    }
}
