package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

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
            String requestUri = readRequestUri(reader);
            if (requestUri == null) {
                return;
            }

            String path = parsePath(requestUri);
            String queryString = parseQueryString(requestUri);
            String redirectLocation = handleLogin(path, queryString);

            if (redirectLocation != null) {
                final var response = String.join("\r\n",
                        "HTTP/1.1 302 Found ",
                        "Location: " + redirectLocation,
                        "Content-Length: 0",
                        "",
                        "");

                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            byte[] responseBody = readResponseBody(path);
            String contentType = resolveContentType(path);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + "charset=utf-8 ",
                    "Content-Length: " + responseBody.length + " ",
                    "",
                    "");

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.write(responseBody);
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String handleLogin(String path, String queryString) {
        if (!"/login".equals(path) || queryString.isEmpty()) {
            return null;
        }

        Map<String, String> parametersByName = new HashMap<>();
        String[] parameters = queryString.split("&");

        for (String parameter : parameters) {
            String[] nameAndValue = parameter.split("=");

            if (nameAndValue.length != 2) {
                return "/401.html";
            }
            parametersByName.put(nameAndValue[0], nameAndValue[1]);
        }

        String account = parametersByName.get("account");
        String password = parametersByName.get("password");

        if (account == null || password == null) {
            return "/401.html";
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> "/index.html")
                .orElse("/401.html");
    }

    private String parsePath(String requestUri) {
        int queryIndex = requestUri.indexOf("?");
        if (queryIndex >= 0) {
            return requestUri.substring(0, queryIndex);
        }
        return requestUri;
    }

    private String parseQueryString(String requestUri) {
        int queryIndex = requestUri.indexOf("?");
        if (queryIndex >= 0) {
            return requestUri.substring(queryIndex + 1);
        }

        return "";
    }

    private String readRequestUri(BufferedReader reader) throws IOException {
        String requestLine = reader.readLine();
        if (requestLine == null) {
            return null;
        }

        while (true) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                return null;
            }

            if (headerLine.isEmpty()) {
                break;
            }
        }

        String[] requestParts = requestLine.split(" ");
        return requestParts[1];
    }

    private String resolveContentType(String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css;";
        }

        return "text/html;";
    }

    private byte[] readResponseBody(String requestUri) throws IOException {
        if ("/".equals(requestUri)) {
            return "Hello world!".getBytes(StandardCharsets.UTF_8);
        }

        if ("/login".equals(requestUri)) {
            requestUri = requestUri + ".html";
        }

        String resourceName = "static" + requestUri;
        try (InputStream resourceStream =
                     Http11Processor.class
                             .getClassLoader()
                             .getResourceAsStream(resourceName)) {

            return Objects.requireNonNull(resourceStream).readAllBytes();
        }
    }
}
