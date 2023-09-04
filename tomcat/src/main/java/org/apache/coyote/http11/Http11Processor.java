package org.apache.coyote.http11;

import nextstep.jwp.db.InMemoryUserRepository;
import nextstep.jwp.exception.UncheckedServletException;
import nextstep.jwp.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
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
            final var inputStreamReader = new InputStreamReader(inputStream);
            final var bufferedReader = new BufferedReader(inputStreamReader);

            final Map<String, String> requestHeader = readRequestHeader(bufferedReader);
            log.info("Request-Header: {}", requestHeader);
            final String httpMethod = requestHeader.get("Request-Line").split(" ")[0];
            final String uri = requestHeader.get("Request-Line").split(" ")[1];

            final String requestBody = readRequestBody(bufferedReader, requestHeader, httpMethod);
            final String response = handleRequest(uri, requestBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestBody(final BufferedReader bufferedReader, final Map<String, String> requestHeader, final String httpMethod) throws IOException {
        if (!httpMethod.equals("POST")) {
            return null;
        }
        final int contentLength = Integer.parseInt(requestHeader.get("Content-Length"));
        final char[] buffer = new char[contentLength];
        bufferedReader.read(buffer, 0, contentLength);
        final String requestBody = new String(buffer);

        log.info("Request-Body: {}", requestBody);
        return requestBody;
    }

    private String handleRequest(final String uri, final String requestBody) throws IOException {
        final String path = uri.split("\\?")[0];

        if (path.equals("/")) {
            return getResponseMessage(200, "Hello world!", "text/html;");
        }
        if (path.endsWith(".css")) {
            final String responseBody = findResponseBody(path);
            return getResponseMessage(200, responseBody, "text/css;");
        }
        if (path.equals("/login")) {
            final String responseBody = findResponseBody(path + ".html");
            return handleLoginRequest(uri, responseBody);
        }
        if (path.equals("/register")) {
            final String responseBody = findResponseBody(path + ".html");
            return handleRegisterRequest(requestBody, responseBody);
        }
        final String responseBody = findResponseBody(path);
        return getResponseMessage(200, responseBody, "text/html;");
    }

    private String handleRegisterRequest(final String requestBody, final String responseBody) throws IOException {
        if (requestBody == null) {
            return getResponseMessage(200, responseBody, "text/html;");
        }
        final Map<String, String> requestBodyValues = new HashMap<>();
        final String[] splitRequestBody = requestBody.split("&");
        for (String value : splitRequestBody) {
            final String[] splitValue = value.split("=");
            requestBodyValues.put(splitValue[0], splitValue[1]);
        }
        final User user = new User(requestBodyValues.get("account"), requestBodyValues.get("password"), requestBodyValues.get("email"));
        InMemoryUserRepository.save(user);
        return getResponseMessage(302, findResponseBody("/index.html"), "text/html;");
    }

    private String handleLoginRequest(final String uri, final String responseBody) throws IOException {
        if (!uri.contains("?")) {
            return getResponseMessage(200, responseBody, "text/html;");
        }
        final Map<String, String> queryStrings = getQueryStrings(uri);
        final Optional<User> user = InMemoryUserRepository.findByAccount(queryStrings.get("account"));
        if (user.isEmpty() || !user.get().checkPassword(queryStrings.get("password"))) {
            return getResponseMessage(401, findResponseBody("/401.html"), "text/html;");
        }
        log.info("User: {}", user.get());
        return getResponseMessage(302, findResponseBody("/index.html"), "text/html;");
    }

    private Map<String, String> getQueryStrings(final String uri) {
        if (!uri.contains("?")) {
            return Map.of();
        }
        final Map<String, String> queryStrings = new HashMap<>();
        final String queryString = uri.split("\\?")[1];

        final String[] splitQueryStrings = queryString.split("&");
        for (String value : splitQueryStrings) {
            final String[] splitValue = value.split("=");
            queryStrings.put(splitValue[0], splitValue[1]);
        }
        return queryStrings;
    }

    private String getResponseMessage(final int statusCode, final String responseBody, final String contentType) {
        return String.join("\r\n",
                "HTTP/1.1 " + statusCode + " OK ",
                "Content-Type: " + contentType + "charset=utf-8 ",
                "Content-Length: " + responseBody.getBytes().length + " ",
                "",
                responseBody);
    }

    private String findResponseBody(final String uri) throws IOException {
        final URL fileUrl = getClass().getClassLoader().getResource("./static" + uri);
        final String filePath = Objects.requireNonNull(fileUrl).getPath();
        return Files.readString(new File(filePath).toPath());
    }

    private Map<String, String> readRequestHeader(final BufferedReader bufferedReader) throws IOException {
        final Map<String, String> requestHeader = new HashMap<>();

        String line = bufferedReader.readLine();
        if (line == null) {
            return Map.of();
        }
        requestHeader.put("Request-Line", line);

        while (!"".equals(line = bufferedReader.readLine())) {
            final String[] splitLine = line.split(": ");
            requestHeader.put(splitLine[0], splitLine[1]);
        }
        return requestHeader;
    }
}
