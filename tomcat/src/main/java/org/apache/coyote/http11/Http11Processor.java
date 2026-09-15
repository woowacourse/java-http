package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
        try (
                final var inputStream = connection.getInputStream();
                final var outputStream = connection.getOutputStream();
                final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))
        ) {
            final String requestLine = bufferedReader.readLine();
            if (requestLine == null) {
                return;
            }

            if (!validateHeaders(bufferedReader)) {
                return;
            }

            final String[] requestComponents = requestLine.split(" ");
            final String requestUri = requestComponents[1];

            final int queryIndex = requestUri.indexOf('?');
            String path;
            final String queryString;
            if (queryIndex >= 0) {
                path = requestUri.substring(0, queryIndex);
                queryString = requestUri.substring(queryIndex + 1);
            } else {
                path = requestUri;
                queryString = "";
            }

            if ("/login".equals(path)) {
                if (!queryString.isBlank()) {
                    final Map<String, String> parameters = parseQueryString(queryString);
                    login(parameters);
                }
                path += ".html";
            }

            final String responseBody = getResponseBody(path);
            final String contentType = resolveContentType(requestUri);

            final var response = String.join("\r\n",
                    "HTTP/1.1 200 OK ",
                    "Content-Type: " + contentType + ";charset=utf-8 ",
                    "Content-Length: " + responseBody.getBytes().length + " ",
                    "",
                    responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String getResponseBody(String path) throws IOException {
        final String responseBody;
        if ("/".equals(path)) {
            responseBody = "Hello world!";
        } else {
            final String resourceName = "static" + path;
            final String fileName = Objects.requireNonNull(
                    getClass().getClassLoader().getResource(resourceName)
            ).getPath();

            responseBody = Files.readString(Path.of(fileName));
        }
        return responseBody;
    }

    private String resolveContentType(final String requestUri) {
        if (requestUri.endsWith(".css")) {
            return "text/css";
        }
        return "text/html";
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();

        if (queryString.isBlank()) {
            return parameters;
        }

        for (String pair : queryString.split("&")) {
            final String[] nameAndValue = pair.split("=", 2);
            if (nameAndValue.length != 2) {
                continue;
            }

            final String name = URLDecoder.decode(
                    nameAndValue[0],
                    StandardCharsets.UTF_8
            );
            final String value = URLDecoder.decode(
                    nameAndValue[1],
                    StandardCharsets.UTF_8
            );

            parameters.put(name, value);
        }

        return parameters;
    }

    private void login(final Map<String, String> parameters) {
        final String account = parameters.get("account");
        final String password = parameters.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("로그인 성공: {}", user));
    }

    private static boolean validateHeaders(BufferedReader bufferedReader) throws IOException {
        String line = bufferedReader.readLine();
        while (!"".equals(line)) {
            if (line == null) {
                return false;
            }
            line = bufferedReader.readLine();
        }
        return true;
    }
}
