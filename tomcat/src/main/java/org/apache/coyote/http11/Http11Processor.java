package org.apache.coyote.http11;

import static java.util.stream.Collectors.toMap;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final Map<String, String> CONTENT_TYPE_MAP = Map.of(
            ".html", "text/html",
            ".css", "text/css",
            ".js", "application/javascript"
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
             final var outputStream = connection.getOutputStream()) {

            final var httpRequestMessage = readMessage(inputStream);
            final var response = createResponse(httpRequestMessage);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readMessage(final InputStream inputStream) throws IOException {
        final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        final var stringBuilder = new StringBuilder();

        while (true) {
            final var line = bufferedReader.readLine();
            if (line == null || line.isEmpty()) {
                break;
            }
            stringBuilder.append(line).append("\r\n");
        }
        return stringBuilder.toString();
    }

    private String createResponse(final String requestMessage) throws IOException {
        final var requestLine = requestMessage.substring(0, requestMessage.indexOf("HTTP/1.1") - 1);
        final var uri = requestLine.split(" ")[1];

        System.out.println("uri = " + uri);
        final var body = createResponseBody(uri);
        System.out.println("body = " + body);
        final var header = createResponseHeader(uri, body);
        return header + "\r\n\r\n" + body;
    }

    private String createResponseBody(final String uri) throws IOException {
        if (uri.startsWith("/login")) {
            final var loginHtml = getClass().getClassLoader().getResource("static/login.html");

            final var queryParams = extractQueryParams(uri);
            if (queryParams.containsKey("account") && queryParams.containsKey("password")) {
                tryLogin(queryParams);
            }

            return Files.readString(Path.of(loginHtml.getPath()));
        }

        final var resource = getClass().getClassLoader().getResource("static" + uri);
        final var file = new File(resource.getFile());
        if (file.exists() && !file.isDirectory()) {
            return Files.readString(file.toPath());
        }

        return "Hello world!";
    }

    private String createResponseHeader(final String uri, final String body) {
        final var extension = uri.lastIndexOf('.') == -1 ? "" : uri.substring(uri.lastIndexOf('.'));
        final var contentType = CONTENT_TYPE_MAP.getOrDefault(extension, "text/html");
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + ";charset=utf-8 ",
                "Content-Length: " + body.getBytes().length + " "
        );
    }

    private void tryLogin(final Map<String, String> queryParams) {
        final var account = queryParams.get("account");
        final var password = queryParams.get("password");
        InMemoryUserRepository.findByAccount(account)
                .filter(u -> u.checkPassword(password))
                .ifPresent(u -> log.info("user : {}", u));
    }

    private Map<String, String> extractQueryParams(final String uri) {
        if (!uri.contains("?")) {
            return Collections.emptyMap();
        }

        final var queryParams = uri.substring(uri.indexOf("?") + 1);
        return Arrays.stream(queryParams.split("&"))
                .map(param -> param.split("="))
                .filter(param -> param.length == 2)
                .collect(toMap(
                        param -> param[0],
                        param -> param[1])
                );
    }
}
