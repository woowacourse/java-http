package org.apache.coyote.http11;

import static java.nio.charset.StandardCharsets.UTF_8;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);
    private static final String STATIC_RESOURCE_PATH = "static";
    private static final String NOT_FOUND_RESOURCE_PATH = "static/404.html";

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, UTF_8));
            final var request = toRequest(reader);
            handleRequest(request, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static Request toRequest(final BufferedReader reader) throws IOException {
        final String requestLine = reader.readLine();
        log.info("request: {}", requestLine);
        final String[] requestParts = requestLine.split(" ", 3);
        final String method = requestParts[0];
        final String requestUri = requestParts[1];
        final Map<String, String> headers = readHeaders(reader);

        final int index = requestUri.indexOf("?");
        final String path = extractPath(index, requestUri);
        final Map<String, String> params = readParams(index, requestUri);

        if ("POST".equals(method)) {
            return new Request(method, path, Map.copyOf(params), extractRequestBody(reader, headers));
        }
        return new Request(method, path, Map.copyOf(params), Map.of());
    }

    private static Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
            final int index = headerLine.indexOf(":");
            if (index > 0) {
                final String name = headerLine.substring(0, index).strip().toLowerCase(Locale.ROOT);
                final String value = headerLine.substring(index + 1).strip();
                headers.put(name, value);
            }
        }
        return headers;
    }

    private static String extractPath(int index, String requestUri) {
        if (index == -1) {
            return requestUri;
        }
        return requestUri.substring(0, index);
    }

    private static Map<String, String> readParams(int index, String requestUri) {
        if (index != -1 && index < requestUri.length() - 1) {
            return parseQueries(requestUri.substring(index + 1));
        }
        return Map.of();
    }

    private static Map<String, String> extractRequestBody(BufferedReader reader, Map<String, String> headers)
            throws IOException {
        String requestBody = readRequestBody(reader, headers);
        final String contentType = headers.getOrDefault("content-type", "");
        if (requestBody.isEmpty() || !contentType.startsWith("application/x-www-form-urlencoded")) {
            return Map.of();
        }
        return parseQueries(requestBody);
    }

    private static String readRequestBody(
            final BufferedReader reader,
            final Map<String, String> headers
    ) throws IOException {
        final int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        final char[] body = new char[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            final int readCount = reader.read(body, offset, contentLength - offset);
            if (readCount == -1) {
                break;
            }
            offset += readCount;
        }
        return new String(body, 0, offset);
    }

    private static Map<String, String> parseQueries(final String queryString) {
        final String[] queries = queryString.split("&");
        final Map<String, String> params = new HashMap<>();
        for (final String query : queries) {
            final String[] pair = query.split("=", 2);
            if (pair.length == 2) {
                final String name = URLDecoder.decode(pair[0].strip(), UTF_8);
                final String value = URLDecoder.decode(pair[1].strip(), UTF_8);
                params.put(name, value);
            }
        }
        return params;
    }

    private void handleRequest(final Request request, final OutputStream outputStream) throws IOException {
        if ("POST".equals(request.method()) && "/register".equals(request.path())) {
            try {
                register(request.body());
                writeResponse(outputStream, "302 Found", Map.of("Location", "/index.html"), new byte[0]);
            } catch (final IllegalArgumentException e) {
                final String failedLocation = "/register.html?error=" + URLEncoder.encode(e.getMessage(), UTF_8);
                writeResponse(outputStream, "302 Found", Map.of("Location", failedLocation), new byte[0]);
            }
        } else if ("POST".equals(request.method()) && "/login".equals(request.path())) {
            try {
                validateAuth(request.body());
                writeResponse(outputStream, "302 Found", Map.of("Location", "/index.html"), new byte[0]);
            } catch (final IllegalArgumentException e) {
                final String invalidRedirectUri = "/login.html?error=" + URLEncoder.encode(e.getMessage(), UTF_8);
                writeResponse(outputStream, "302 Found", Map.of("Location", invalidRedirectUri), new byte[0]);
            }
        } else if ("/".equals(request.path())) {
            final var body = "Hello world!".getBytes(UTF_8);
            writeResponse(outputStream, "200 OK", contentTypeHeader("text/html"), body);
        } else {
            writeResource(outputStream, "200 OK", STATIC_RESOURCE_PATH + appendHtmlExtension(request.path()));
        }
    }

    private void register(final Map<String, String> params) {
        final String account = params.get("account");
        final String password = params.get("password");
        final String email = params.get("email");
        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("계정 정보가 비어있다.");
        }
        if (InMemoryUserRepository.existsByAccount(account)) {
            throw new IllegalArgumentException("계정이 존재한다.");
        }
        InMemoryUserRepository.save(new User(account, password, email));
    }

    private void validateAuth(final Map<String, String> params) {
        String account = params.get("account");
        boolean invalid = account == null
                || InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(params.get("password")))
                .isEmpty();

        if (invalid) {
            throw new IllegalArgumentException("로그인 정보가 잘못됐다.");
        }
    }

    private void writeResource(
            final OutputStream outputStream,
            final String status,
            final String resourcePath
    ) throws IOException {
        try (InputStream resource = getClass().getClassLoader().getResourceAsStream(resourcePath)) {
            if (resource == null) {
                writeResource(outputStream, "404 Not Found", NOT_FOUND_RESOURCE_PATH);
                return;
            }

            final var contentType = MimeTypeResolver.resolve(resourcePath);
            writeResponse(outputStream, status, contentTypeHeader(contentType), resource.readAllBytes());
        }
    }

    private void writeResponse(
            final OutputStream outputStream,
            final String status,
            final Map<String, String> headers,
            final byte[] responseBody
    ) throws IOException {
        final var responseHeader = new StringBuilder().append("HTTP/1.1 %s \r\n".formatted(status));
        headers.forEach((name, value) -> responseHeader.append("%s: %s \r\n".formatted(name, value)));
        responseHeader.append("Content-Length: %d \r\n".formatted(responseBody.length)).append("\r\n");

        outputStream.write(responseHeader.toString().getBytes(UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }

    private Map<String, String> contentTypeHeader(final String contentType) {
        if (contentType.startsWith("text/")) {
            return Map.of("Content-Type", contentType + ";charset=utf-8");
        }
        return Map.of("Content-Type", contentType);
    }

    private static String appendHtmlExtension(final String resourcePath) {
        final var fileName = resourcePath.substring(resourcePath.lastIndexOf('/') + 1);
        if (fileName.contains(".")) {
            return resourcePath;
        }
        return resourcePath + ".html";
    }
}
