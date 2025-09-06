package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String[] requestInfo = parseRequestLine(requestLine);
            if (requestInfo == null) {
                return;
            }

            final String response = handleRequest(requestInfo);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String[] parseRequestLine(final String requestLine) {
        final String[] requestLineArray = requestLine.split(" ");
        if (requestLineArray.length < 2) {
            return null;
        }

        final String uri = requestLineArray[1];
        final int index = uri.indexOf("?");
        final String path = (index != -1) ? uri.substring(0, index) : uri;
        final String queryString = (index != -1) ? uri.substring(index + 1) : "";

        return new String[]{path, queryString};
    }

    private String handleRequest(final String[] requestInfo) throws IOException, URISyntaxException {
        String path = requestInfo[0];
        final String queryString = requestInfo[1];

        if ("/login".equals(path)) {
            handleLogin(queryString);
            path += ".html";
        }

        final byte[] bytes = readFile(path);
        return generateResponse(path, bytes);
    }

    private void handleLogin(final String queryString) {
        final Map<String, String> parameters = parseQueryString(queryString);
        if (login(parameters)) {
            log.info("login successful");
            return;
        }
        log.info("login failed");
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            final String[] pairs = queryString.split("&");
            for (final String pair : pairs) {
                final String[] keyValue = pair.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        return parameters;
    }

    private boolean login(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            return false;
        }

        final User user = InMemoryUserRepository.findByAccount(account)
                .orElse(null);

        if (user == null) {
            return false;
        }

        if (user.checkPassword(password)) {
            log.info("user: {}", user);
            return true;
        }
        return false;
    }

    private byte[] readFile(final String path) throws IOException, URISyntaxException {
        final URL resource = getClass().getClassLoader().getResource("static" + path);
        final Path filePath = Paths.get(resource.toURI());
        return Files.readAllBytes(filePath);
    }

    private String generateResponse(final String path, final byte[] bytes) {
        final String responseBody = new String(bytes);
        final String contentType = getContentType(path);

        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + bytes.length + " ",
                "",
                responseBody);
    }

    private String getContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }
}
