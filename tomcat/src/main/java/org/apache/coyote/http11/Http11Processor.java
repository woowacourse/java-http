package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
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

            final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));

            final String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            final String uri = extractUri(requestLine);
            final String path = extractPath(uri);
            final Map<String, String> queryParams = extractQueryParams(uri);

            final String resourcePath = resolveResourcePath(path, queryParams);
            final String responseBody = createResponseBody(path, resourcePath);

            final String response = createResponse(uri, responseBody);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private String extractUri(final String requestLine) {
        return requestLine.split(" ")[1];
    }

    private String extractPath(final String uri) {
        final int index = uri.indexOf("?");

        if (index == -1) {
            return uri;
        }

        return uri.substring(0, index);
    }

    private Map<String, String> extractQueryParams(final String uri) {
        final Map<String, String> queryParams = new HashMap<>();

        final int index = uri.indexOf("?");

        if (index == -1) {
            return queryParams;
        }

        final String queryString = uri.substring(index + 1);

        for (String parameter : queryString.split("&")) {
            final String[] keyValue = parameter.split("=", 2);

            if (keyValue.length == 2) {
                queryParams.put(keyValue[0], keyValue[1]);
            }
        }

        return queryParams;
    }

    private String resolveResourcePath(
            final String path,
            final Map<String, String> queryParams
    ) {
        if (!"/login".equals(path)) {
            return path;
        }

        if (!queryParams.isEmpty()) {
            final String account = queryParams.get("account");
            final String password = queryParams.get("password");

            InMemoryUserRepository.findByAccount(account)
                    .filter(user -> user.checkPassword(password))
                    .ifPresent(user ->
                            log.info("회원 조회 결과: {}", user)
                    );
        }

        return "/login.html";
    }

    private String createResponseBody(
            final String path,
            final String resourcePath
    ) throws IOException, URISyntaxException {

        if ("/".equals(path)) {
            return "Hello world!";
        }

        final ClassLoader classLoader = getClass().getClassLoader();
        final URL resource = classLoader.getResource("static" + resourcePath);

        if (resource == null) {
            return "";
        }

        final URI fileUri = resource.toURI();
        final Path filePath = Paths.get(fileUri);
        final byte[] fileBytes = Files.readAllBytes(filePath);

        return new String(fileBytes, StandardCharsets.UTF_8);
    }

    private String createResponse(
            final String uri,
            final String responseBody
    ) {
        final String contentType = resolveContentType(uri);
        final byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);

        return String.join(
                "\r\n",
                "HTTP/1.1 200 OK",
                "Content-Type: " + contentType,
                "Content-Length: " + body.length,
                "",
                responseBody
        );
    }

    private String resolveContentType(final String uri) {
        if (uri.endsWith(".css")) {
            return "text/css";
        }

        if (uri.endsWith(".js")) {
            return "application/javascript";
        }

        return "text/html;charset=utf-8";
    }
}
