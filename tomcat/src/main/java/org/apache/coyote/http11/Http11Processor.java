package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String ROOT_PATH = "/";
    private static final String LOGIN_PATH = "/login";
    private static final String DEFAULT_RESPONSE_BODY = "Hello world!";
    private static final String DEFAULT_CONTENT_TYPE = "text/html;charset=utf-8";
    private static final String STATIC_RESOURCE_DIRECTORY = "static";

    private static final Map<String, String> RESOURCE_PATH_BY_REQUEST_PATH = Map.of(
            LOGIN_PATH,  "/login.html"
    );

    private static final Map<String, String> CONTENT_TYPE_BY_EXTENSION = Map.of(
            ".html", "text/html;charset=utf-8",
            ".css", "text/css;charset=utf-8",
            ".js", "application/javascript;charset=utf-8"
    );

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
             final var outputStream = connection.getOutputStream();
             final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {

            final String requestLine = reader.readLine();
            final String requestUri = requestLine.split(" ")[1];

            final int queryStringIndex = requestUri.indexOf("?");

            final String path;
            final String queryString;

            if (queryStringIndex == -1) {
                path = requestUri;
                queryString = "";
            } else {
                path = requestUri.substring(0, queryStringIndex);
                queryString = requestUri.substring(queryStringIndex + 1);
            }

            skipHeaders(reader);

            if (LOGIN_PATH.equals(path) && !queryString.isEmpty()) {
                login(queryString);
            }

            final ResponseData responseData = loadResponseData(path);
            outputStream.write(
                    response(responseData.body(), responseData.contentType()).getBytes(StandardCharsets.UTF_8)
            );

            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private ResponseData loadResponseData(final String requestPath) throws IOException {
        if (ROOT_PATH.equals(requestPath)) {
            return new ResponseData(
                    DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8),
                    DEFAULT_CONTENT_TYPE
            );
        }

        final String resourcePath = STATIC_RESOURCE_DIRECTORY + RESOURCE_PATH_BY_REQUEST_PATH.getOrDefault(requestPath, requestPath);

        final InputStream resource = getClass().
                getClassLoader().
                getResourceAsStream(resourcePath);

        if (resource == null) {
            return new ResponseData(
                    DEFAULT_RESPONSE_BODY.getBytes(StandardCharsets.UTF_8),
                    DEFAULT_CONTENT_TYPE
            );
        }

        try (resource) {
            return new ResponseData(resource.readAllBytes(), contentTypeOf(resourcePath));
        }
    }

    private String contentTypeOf(final String resourcePath) {
        return CONTENT_TYPE_BY_EXTENSION.entrySet().stream()
                .filter(entry -> resourcePath.endsWith(entry.getKey()))
                .map(Map.Entry::getValue)
                .findFirst()
                .orElse(DEFAULT_CONTENT_TYPE);
    }

    private record ResponseData(byte[] body, String contentType) {
    }

    private void skipHeaders(final BufferedReader reader) throws IOException {
        while (true) {
            final String headerLine = reader.readLine();

            if (headerLine == null || headerLine.isEmpty()) {
                return;
            }
        }
    }

    private String response(final byte[] responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.length + " ",
                "",
                new String(responseBody, StandardCharsets.UTF_8));
    }

    private void login(final String queryString) {
        if (queryString.isEmpty()) {
            return;
        }

        final Map<String, String> parameters = Arrays.stream(queryString.split("&"))
                .map(parameter -> parameter.split("=", 2))
                .collect(Collectors.toMap(
                        parameter -> parameter[0],
                        parameter -> parameter.length > 1 ? parameter[1] : ""
                ));

        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            return;
        }

        final Optional<User> user = InMemoryUserRepository.findByAccount(account);

        if (user.isPresent() && user.get().checkPassword(password)) {
            log.info("로그인한 회원: {}", user.get());
        }
    }
}
