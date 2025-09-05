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
            final var reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String[] requestLineArray = requestLine.split(" ");
            final String uri = requestLineArray[1];

            final int index = uri.indexOf("?");
            String path = (index != -1) ? uri.substring(0, index) : uri;
            final String queryString = (index != -1) ? uri.substring(index + 1) : "";

            if ("/login".equals(path)) {
                handleLogin(queryString);
                path += ".html";
            }

            final URL resource = getClass().getClassLoader().getResource("static" + path);
            final Path filePath = Paths.get(resource.toURI());
            final byte[] bytes = Files.readAllBytes(filePath);
            final String responseBody = new String(bytes);
            String contentType = "text/html";
            if (path.endsWith(".css")) {
                contentType = "text/css";
            }

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + bytes.length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException | URISyntaxException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleLogin(String queryString) {
        Map<String, String> parameters = parseQueryString(queryString);
        String account = parameters.get("account");
        String password = parameters.get("password");

        if (account != null && password != null) {
            User user = InMemoryUserRepository.findByAccount(account)
                    .orElseThrow();
            if (user.checkPassword(password)) {
                log.info("login successful");
                log.info("user: {}", user);
                return;
            }
        }
        log.info("login failed");
    }

    private Map<String, String> parseQueryString(String queryString) {
        Map<String, String> parameters = new HashMap<>();
        if (queryString != null && !queryString.isEmpty()) {
            String[] pairs = queryString.split("&");
            for (String pair : pairs) {
                String[] keyValue = pair.split("=");
                parameters.put(keyValue[0], keyValue[1]);
            }
        }
        return parameters;
    }
}
