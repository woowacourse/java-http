package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
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
            final var bufferedReader = new BufferedReader(new InputStreamReader(inputStream))) {

            String[] requestMessage = extractRequestMessage(bufferedReader);
            final Map<String, String> headers = extractHeaders(bufferedReader);
            String body = extractRequestBody(bufferedReader, headers);

            String method = requestMessage[0];
            String requestTarget = requestMessage[1];
            String requestPath = extractRequestPath(requestTarget);
            Map<String, String> queryParameters = parseQueryParameters(requestTarget);

            Optional<String> redirectPath = dispatchRequest(method, requestPath, queryParameters, body);
            if (redirectPath.isPresent()) {
                outputStream.write(createRedirectResponse(redirectPath.get()).getBytes());
                outputStream.flush();
                return;
            }

            String resourcePath = resolveResourcePath(requestPath);
            String responseBody = resolveResponseBody(resourcePath);
            String contentType = resolveContentType(resourcePath);
            String header = createHeader(contentType, responseBody);

            String response = createResponse(header, responseBody);

            outputStream.write(response.getBytes());
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String extractRequestBody(BufferedReader bufferedReader, Map<String, String> headers) throws IOException {
        final String contentLength = headers.get("Content-Length");
        if (contentLength == null) {
            return "";
        }
        final char[] buffer = new char[Integer.parseInt(contentLength)];
        bufferedReader.read(buffer);
        return new String(buffer);
    }

    private String createHeader(String contentType, String responseBody) {
        return String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + responseBody.getBytes().length + " ");
    }

    private static String createResponse(String header, String responseBody) {
        return String.join("\r\n",
                header,
                "",
                responseBody);
    }

    private static String createRedirectResponse(String redirectPath) {
        String header = String.join("\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + redirectPath,
                "Content-Length: 0");

        return createResponse(header, "");

    }

    private Optional<String> dispatchRequest(
            String method,
            String requestPath,
            Map<String, String> queryParameters,
            String body) {
        if ("GET".equals(method) && "/login".equals(requestPath) && !queryParameters.isEmpty()) {
            return Optional.of(handleLogin(queryParameters.get("account"), queryParameters.get("password")));
        }
        if ("POST".equals(method) && "/register".equals(requestPath)) {
            return Optional.of(handleRegister(body));
        }
        return Optional.empty();
    }

    private String handleRegister(String body) {
        final Map<String, String> params = parseParams(body);
        User user = new User(
                params.get("account"),
                params.get("password"),
                params.get("email")
        );
        InMemoryUserRepository.save(user);
        return "/index.html";
    }

    private Map<String, String> parseParams(String body) {
        String[] parameterPairs = body.split("&");

        return Arrays.stream(parameterPairs)
                .map(parameterPair -> parameterPair.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private String handleLogin(String account, String password) {
        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    log.info("user={}", user);
                    return "/index.html";
                })
                .orElse("/401.html");
    }

    private String extractRequestPath(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf('?');

        if (queryStartIndex < 0) {
            queryStartIndex = requestTarget.length();
        }

        return requestTarget.substring(0, queryStartIndex);
    }

    private Map<String, String> parseQueryParameters(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf('?');

        if (queryStartIndex < 0) {
            return Map.of();
        }

        String queryString = requestTarget.substring(queryStartIndex + 1);
        String[] parameterPairs = queryString.split("&");

        return Arrays.stream(parameterPairs)
                .map(parameterPair -> parameterPair.split("="))
                .collect(Collectors.toMap(s -> s[0], s -> s[1]));
    }

    private String resolveResourcePath(String requestPath) {
        if ("/".equals(requestPath)) {
            return "/";
        }
        if (!requestPath.contains(".")) {
            requestPath = requestPath + ".html";
        }
        return "static" + requestPath;
    }

    private String[] extractRequestMessage(BufferedReader bufferedReader) throws IOException {
        String requestLine = bufferedReader.readLine();

        return requestLine.split(" ");
    }

    private String resolveResponseBody(String resourcePath) throws IOException {
        if ("/".equals(resourcePath)) {
            return "Hello world!";
        }
        URL resourceUrl = getClass().getClassLoader().getResource(resourcePath);
        if (resourceUrl == null) {
            throw new RuntimeException("resource not found");
        }
        return readStaticResource(resourceUrl);
    }

    private String readStaticResource(URL resourceUrl) throws IOException {
        Path filePath = new File(resourceUrl.getPath()).toPath();
        return Files.readString(filePath);
    }

    private String resolveContentType(String resourcePath) {
        if (resourcePath.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        if (resourcePath.endsWith(".js")) {
            return "application/javascript;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private Map<String, String> extractHeaders(final BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            String[] parts = headerLine.split(":", 2);
            headers.put(parts[0], parts[1].trim());
        }

        return headers;
    }
}
