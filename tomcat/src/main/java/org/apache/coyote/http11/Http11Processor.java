package org.apache.coyote.http11;

import com.techcourse.exception.UncheckedServletException;
import com.techcourse.db.InMemoryUserRepository;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
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

            final var bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }

            final String requestUri = requestLine.split(" ")[1];
            final String requestPath = requestPath(requestUri);

            if ("/login".equals(requestPath) && requestUri.contains("?")) {
                final boolean success = logIn(requestPath, requestUri);
                final String location = success ? "/index.html" : "/401.html";
                final String redirectResponse = String.join("\r\n",
                        "HTTP/1.1 302 Found",
                        "Location: " + location,
                        "Content-Length: 0",
                        "",
                        "");
                outputStream.write(redirectResponse.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final String responseBody = responseBody(requestPath);
            final String contentType = contentType(requestPath);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + " ",
                    "Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String requestPath(final String requestUri) {
        final int queryStringIndex = requestUri.indexOf("?");
        if (queryStringIndex < 0) {
            return requestUri;
        }
        return requestUri.substring(0, queryStringIndex);
    }

    private boolean logIn(final String requestPath, final String requestUri) {
        final int queryStringIndex = requestUri.indexOf("?");
        if (!"/login".equals(requestPath) || queryStringIndex < 0) {
            return false;
        }

        final String queryString = requestUri.substring(queryStringIndex + 1);
        final Map<String, String> queryParameters = queryParameters(queryString);
        final String account = queryParameters.get("account");
        final String password = queryParameters.get("password");
        if (account == null || password == null) {
            return false;
        }

        final var user = InMemoryUserRepository.findByAccount(account);
        if (user.isEmpty() || !user.get().checkPassword(password)) {
            return false;
        }
        log.info("login user: {}", account);
        return true;
    }

    private Map<String, String> queryParameters(final String queryString) {
        final Map<String, String> queryParameters = new HashMap<>();
        for (String parameter : queryString.split("&")) {
            final String[] nameAndValue = parameter.split("=", 2);
            if (nameAndValue.length == 2) {
                queryParameters.put(nameAndValue[0], nameAndValue[1]);
            }
        }
        return queryParameters;
    }

    private String responseBody(final String requestPath) throws IOException {
        if ("/".equals(requestPath)) {
            return "Hello world!";
        }

        final String resourcePath = "/login".equals(requestPath) ? "/login.html" : requestPath;
        final URL resource = getClass().getClassLoader().getResource("static" + resourcePath);
        if (resource == null) {
            return "";
        }

        return Files.readString(Path.of(resource.getPath()), StandardCharsets.UTF_8);
    }

    private String contentType(final String requestPath) {
        if (requestPath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }
}
