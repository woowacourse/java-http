package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

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
            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.US_ASCII));

            final var requestLine = parseRequestLine(reader);
            final var headers = readHeaders(reader);

            final var cookieHeader = headers.get("Cookie");
            final var cookie = HttpCookie.parse(cookieHeader);

            final var method = requestLine.get(0);
            final var requestUri = requestLine.get(1);

            final Map<String, String> requestParameters;
            if ("POST".equals(method)) {
                final var contentLengthHeader = headers.get("Content-Length");

                if (contentLengthHeader == null) {
                    throw new IllegalArgumentException("Content-Length 헤더가 없습니다.");
                }

                final var contentLength = Integer.parseInt(contentLengthHeader);
                final var requestBody = readRequestBody(reader, contentLength);
                requestParameters = requestBody.isEmpty()
                        ? Map.of()
                        : parseParameters(requestBody);
            } else {
                requestParameters = parseQueryString(requestUri);
            }

            final var requestPath = extractPath(requestUri);
            final var contentType = determineContentType(requestPath);

            if (requestPath.equals("/")) {
                final var responseBody = "Hello world!".getBytes(StandardCharsets.UTF_8);
                writeResponse(outputStream, responseBody, contentType);
                return;
            }

            if (requestPath.equals("/login")) {
                handleLogin(method, requestParameters, outputStream, contentType, cookie);
                return;
            }

            if (requestPath.equals("/register")) {
                handleRegister(method, requestParameters, outputStream, contentType);
                return;
            }

            final var responseBody = readStaticResource(requestPath);
            writeResponse(outputStream, responseBody, contentType);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private void handleLogin(final String method, final Map<String, String> parameters, final OutputStream outputStream, final String contentType, final HttpCookie cookie) throws IOException {
        if ("GET".equals(method)) {
            final var responseBody = readStaticResource("/login.html");
            writeResponse(outputStream, responseBody, contentType);
            return;
        }

        if ("POST".equals(method)) {
            if (!authenticate(parameters)) {
                writeRedirectResponse(outputStream, "/401.html");
                return;
            }

            final var sessionId = cookie.getValue("JSESSIONID");

            if (sessionId.isPresent()) {
                writeRedirectResponse(outputStream, "/index.html");
                return;
            }

            writeRedirectResponse(outputStream, "/index.html", UUID.randomUUID().toString());
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + method);
    }

    private void handleRegister(final String method, final Map<String, String> parameters, final OutputStream outputStream, final String contentType) throws IOException {
        if ("GET".equals(method)) {
            final var responseBody = readStaticResource("/register.html");
            writeResponse(outputStream, responseBody, contentType);
            return;
        }

        if ("POST".equals(method)) {
            register(parameters);
            writeRedirectResponse(outputStream, "/index.html");
            return;
        }

        throw new IllegalArgumentException("지원하지 않는 HTTP 메서드입니다: " + method);
    }

    private List<String> parseRequestLine(final BufferedReader reader) throws IOException {
        List<String> resultLines = new ArrayList<>();
        final var line = reader.readLine();

        if (line == null || line.isEmpty()) {
            throw new IllegalArgumentException("HTTP 요청 라인이 비어있습니다.");
        }

        final var tokens = line.trim().split("\\s+");

        if (tokens.length != 3) {
            throw new IllegalArgumentException("올바르지 않은 HTTP 요청 라인입니다.");
        }

        resultLines.add(tokens[0]);
        resultLines.add(tokens[1]);

        return resultLines;
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final var resultMap = new HashMap<String, String>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] split = line.trim().split(":", 2);

            if (split.length != 2) {
                throw new IllegalArgumentException("올바르지 않은 HTTP 요청입니다.");
            }
            resultMap.put(split[0].trim(), split[1].trim());
        }
        return resultMap;
    }

    private String readRequestBody(final BufferedReader reader, final int contentLength) throws IOException {
        if (contentLength < 0) {
            throw new IllegalArgumentException("Content-Length는 음수일 수 없습니다.");
        }

        final var buffer = new char[contentLength];
        var offset = 0;

        while (offset < contentLength) {
            final var readLength = reader.read(buffer, offset, contentLength - offset);

            if (readLength == -1) {
                throw new IllegalArgumentException("요청 Body가 Content-Length보다 짧습니다.");
            }

            offset += readLength;
        }

        return new String(buffer);
    }

    private byte[] readStaticResource(final String path) throws IOException {
        final var resourcePath = "static" + path;

        final var resource = getClass().getClassLoader().getResourceAsStream(resourcePath);

        if (resource == null) {
            throw new FileNotFoundException("파일을 찾을 수 없습니다." + resourcePath);
        }

        try (resource) {
            return resource.readAllBytes();
        }
    }

    private void writeResponse(final OutputStream outputStream, final byte[] bytes, String contentType) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 200 OK ",
                "Content-Type: " + contentType + " ",
                "Content-Length: " + bytes.length + " ",
                "\r\n");
        outputStream.write(response.getBytes());
        outputStream.write(bytes);
        outputStream.flush();
    }

    private void writeRedirectResponse(final OutputStream outputStream, final String location, final String sessionId) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location,
                "Set-Cookie: JSESSIONID=" + sessionId,
                "\r\n");
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private void writeRedirectResponse(final OutputStream outputStream, final String location) throws IOException {
        final var response = String.join("\r\n",
                "HTTP/1.1 302 Found ",
                "Location: " + location,
                "\r\n");
        outputStream.write(response.getBytes());
        outputStream.flush();
    }

    private String determineContentType(final String path) {
        if (path.endsWith(".css")) {
            return "text/css;charset=utf-8";
        }
        return "text/html;charset=utf-8";
    }

    private String extractPath(final String uri) {
        String[] parsedUri = uri.split("\\?");
        return parsedUri[0];
    }

    private Map<String, String> parseQueryString(final String uri) {
        final var queryIndex = uri.indexOf('?');

        if (queryIndex < 0 || queryIndex == uri.length() - 1) {
            return Map.of();
        }

        final var queryString = uri.substring(queryIndex + 1);

        return parseParameters(queryString);
    }

    private Map<String, String> parseParameters(final String parameters) {
        return Arrays.stream(parameters.split("&"))
                .map(this::parseParameter)
                .collect(Collectors.toMap(
                        pair -> decodeParameter(pair[0]),
                        pair -> decodeParameter(pair[1])
                ));
    }

    private String[] parseParameter(final String parameter) {
        final var pair = parameter.split("=", 2);

        if (pair.length != 2 || pair[0].isEmpty()) {
            throw new IllegalArgumentException("잘못된 요청 파라미터입니다. " + parameter);
        }

        return pair;
    }

    private String decodeParameter(final String parameter) {
        return URLDecoder.decode(parameter, StandardCharsets.UTF_8);
    }

    private boolean authenticate(Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");

        if (account == null || password == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .map(user -> user.checkPassword(password))
                .orElse(false);
    }

    private void register(final Map<String, String> parameters) {
        final var account = parameters.get("account");
        final var password = parameters.get("password");
        final var email = parameters.get("email");

        if (account == null || password == null || email == null) {
            throw new IllegalArgumentException("회원가입 정보가 올바르지 않습니다.");
        }

        InMemoryUserRepository.save(new User(account, password, email));
    }
}
