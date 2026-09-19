package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_URI = "/";
    private static final String STATIC_RESOURCE_ROOT = "static";
    private static final String LOGIN = "/login";
    private static final String DOT_HTML = ".html";

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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }
            final String[] requestParts = requestLine.split(" ");
            final String method = requestParts[0];
            String requestUri = requestParts[1];
            final String version = requestParts[2];
            final Map<String, String> headers = readHeaders(reader);

            byte[] responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
            String contentType = getContentType(requestUri);

            String queryString;
            final int queryIndex = requestUri.indexOf('?');
            if (queryIndex != -1) {
                queryString = requestUri.substring(queryIndex + 1);
                requestUri = requestUri.substring(0, queryIndex);
                String[] queryStringParts = queryString.split("&");
                checkUser(queryStringParts);
            }

            if (requestUri.equals(LOGIN)) {
                requestUri += DOT_HTML;
            }

            if (!requestUri.equals(ROOT_URI)) {
                final Path path = Path.of(getResourcePath(STATIC_RESOURCE_ROOT + requestUri));
                responseBody = Files.readAllBytes(path);
            }

            final String responseHeader = createResponseHeader(version, contentType, responseBody.length);

            outputStream.write(responseHeader.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final String[] header = line.split(":", 2);
            final String name = header[0].trim();
            final String value = header[1].trim();
            headers.put(name, value);
        }
        return headers;
    }

    private String createResponseHeader(final String version, String contentType, final int contentLength) {
        return String.join("\r\n",
                version + " 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + contentLength + " ",
                "",
                "");
    }

    private String getResourcePath(String path) {
        return getClass().getClassLoader()
                .getResource(path)
                .getPath();
    }

    private String getContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        if (requestUri.endsWith(".js")) {
            return "text/javascript";
        }
        return "text/html;charset=utf-8";
    }

    private void checkUser(final String[] requestParts) {
        final String account = requestParts[0].split("=", 2)[1];
        String password = requestParts[1].split("=", 2)[1];

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isEmpty()) {
            return;
        }

        if (user.get().checkPassword(password)) {
            System.out.println(user.get());
        }
    }
}
