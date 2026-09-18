package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

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

            final var reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            // 1. Request Line
            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            final String uri = extractUri(requestLine);    // GET /login?account=gugu&password=password HTTP/1.1
            if (uri == null) {
                return;
            }

            // 2. path와 query string 분리
            final String path = extractPath(uri);
            final String queryString = extractQueryString(uri);


            // 3. 로그인 요청 + Query String이 있으면 회원 조회
            logUserIfLoginRequest(path, queryString);

            // 4. Response Body 결정
            if ("/".equals(path)) {
                writeResponse(
                        outputStream,
                        "Hello world!",
                        "text/html;charset=utf-8"
                );
                return;
            }

            writeStaticResource(outputStream, path);

        } catch (IOException
                 | URISyntaxException
                 | UncheckedServletException e) {

            log.error(e.getMessage(), e);
        }
    }

    private String extractUri(final String requestLine) {
        final String[] parts = requestLine.split(" ", 3);

        if (parts.length < 2) {
            return null;
        }

        return parts[1];
    }

    private String extractPath(final String uri) {
        final int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            return uri;
        }

        return uri.substring(0, queryIndex);
    }

    private String extractQueryString(final String uri) {
        final int queryIndex = uri.indexOf("?");

        if (queryIndex == -1) {
            return null;
        }

        return uri.substring(queryIndex + 1);
    }

    private void logUserIfLoginRequest(
            final String path,
            final String queryString
    ) {
        if (!"/login".equals(path)) {
            return;
        }

        if (queryString == null || queryString.isBlank()) {
            return;
        }

        final Map<String, String> parameters =
                parseQueryString(queryString);

        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            return;
        }

        final Optional<User> user =
                InMemoryUserRepository.findByAccount(account);

        user.filter(foundUser ->
                        foundUser.checkPassword(password))
                .ifPresent(foundUser ->
                        log.info("user: {}", foundUser));
    }

    private Map<String, String> parseQueryString(
            final String queryString
    ) {
        final Map<String, String> parameters =
                new HashMap<>();

        for (String parameter : queryString.split("&")) {
            final String[] pair = parameter.split("=", 2);

            if (pair.length != 2) {
                continue;
            }

            parameters.put(pair[0], pair[1]);
        }

        return parameters;
    }

    private void writeStaticResource(
            final OutputStream outputStream,
            final String path
    ) throws IOException, URISyntaxException {

        final String resourcePath = resolveResourcePath(path);

        final URL resource = getClass()
                .getClassLoader()
                .getResource(resourcePath);

        final Path resourceFile =
                Path.of(resource.toURI());

        final String responseBody =
                Files.readString(
                        resourceFile,
                        StandardCharsets.UTF_8
                );

        final String contentType = resolveContentType(path);

        writeResponse(outputStream, responseBody, contentType);
    }

    private String resolveResourcePath(final String path) {
        if ("/login".equals(path)) {
            return "static/login.html";
        }

        return "static" + path;
    }

    private String resolveContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css";
        }

        if (path.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String responseBody,
            final String contentType
    ) throws IOException {

        final byte[] responseBodyBytes =
                responseBody.getBytes(StandardCharsets.UTF_8);

        final String response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBodyBytes.length + " ",
                "",
                responseBody
        );

        outputStream.write(
                response.getBytes(StandardCharsets.UTF_8)
        );

        outputStream.flush();
    }


}