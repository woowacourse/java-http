package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URI;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Http11Processor implements Runnable, Processor {

    private static final String JSESSIONID = "JSESSIONID";
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

            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );

            String requestLine = reader.readLine();

            if (requestLine == null) {
                return;
            }

            String[] requestParts = requestLine.trim().split("\\s+");

            String method = requestParts[0];
            URI uri = URI.create(requestParts[1]);

            Map<String, String> headers = readHeaders(reader);
            String body = readBody(reader, headers);

            Cookie cookie = Cookie.from(headers.get("cookie"));
            handleRequest(method, uri, body, cookie, outputStream);

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();

        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] parts = line.split(":", 2);
            headers.put(
                    parts[0].trim().toLowerCase(),
                    parts[1].trim()
            );
        }
        return headers;
    }

    private String readBody(BufferedReader reader, Map<String, String> headers) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));

        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int read = reader.read(buffer, totalRead, contentLength - totalRead);

            if (read == -1) {
                break;
            }

            totalRead += read;
        }

        return new String(buffer, 0, totalRead);
    }

    private void handleRequest(String method, URI uri, String body, Cookie cookie, OutputStream outputStream) throws IOException {
        String path = uri.getPath();

        if ("/".equals(path)) {
            writeResponse(outputStream, "static/index.html", cookie);
            return;
        }

        if ("/login".equals(path)) {
            if ("GET".equals(method)) {
                writeResponse(outputStream, "static/login.html", cookie);
                return;
            }

            if ("POST".equals(method)) {
                handleLogin(body);
                writeRedirectResponse(outputStream, "/index.html", cookie);
                return;
            }

            writeRedirectResponse(outputStream, "/401.html", cookie);
            return;
        }

        if ("/register".equals(path)) {
            if ("GET".equals(method)) {
                writeResponse(outputStream, "static/register.html", cookie);
                return;
            }

            if ("POST".equals(method)) {
                handleRegister(body);
                writeRedirectResponse(outputStream, "/index.html", cookie);
                return;
            }
        }

        writeResponse(outputStream, "static" + path, cookie);
    }

    private void handleLogin(String body) {
        Map<String, String> params = parseParameters(body);

        String account = params.get("account");
        String password = params.get("password");

        if (account == null || password == null) {
            return;
        }

        InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .ifPresent(user -> log.info("로그인 성공! : {}", user.getAccount()));
    }

    private void handleRegister(String body) {
        Map<String, String> params = parseParameters(body);

        String account = params.get("account");
        String password = params.get("password");
        String email = params.get("email");

        if (account == null || password == null || email == null) {
            return;
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private Map<String, String> parseParameters(String parameter) {
        if (parameter == null || parameter.isBlank()) {
            return Map.of();
        }
        return Arrays.stream(parameter.split("&"))
                .map(param -> param.split("=", 2))
                .filter(parts -> parts.length == 2)
                .collect(Collectors.toMap(
                        parts -> decode(parts[0]),
                        parts -> decode(parts[1])
                ));
    }

    private void setCookieHeader(List<String> responseHeader, Cookie cookie) {
        if (cookie.contains(JSESSIONID)) {
            return;
        }

        String sessionId = UUID.randomUUID().toString();

        responseHeader.add(
                "Set-Cookie: " + JSESSIONID + "=" + sessionId
        );
    }

    private void writeResponse(OutputStream outputStream, String resourcePath, Cookie cookie) throws IOException {
        String contentType = URLConnection.guessContentTypeFromName(resourcePath);

        try (InputStream resource = getClass()
                .getClassLoader()
                .getResourceAsStream(resourcePath)) {

            if (resource == null) {
                log.warn("리소스를 찾을 수 없습니다. : {}", resourcePath);
                return;
            }

            byte[] responseBody = resource.readAllBytes();

            List<String> responseHeaders = new ArrayList<>();

            responseHeaders.add("HTTP/1.1 200 OK");
            setCookieHeader(responseHeaders, cookie);
            responseHeaders.add("Content-Type: " + contentType + ";charset=utf-8 ");
            responseHeaders.add("Content-Length: " + responseBody.length + " ");
            responseHeaders.add("");
            responseHeaders.add("");

            outputStream.write(
                    String.join("\r\n", responseHeaders)
                            .getBytes(StandardCharsets.UTF_8)
            );
            outputStream.write(responseBody);
            outputStream.flush();
        }
    }

    private void writeRedirectResponse(OutputStream outputStream, String location, Cookie cookie) throws IOException {
        List<String> responseHeaders = new ArrayList<>();

        responseHeaders.add("HTTP/1.1 302 Found");
        setCookieHeader(responseHeaders, cookie);
        responseHeaders.add("Location: " + location);
        responseHeaders.add("Content-Length: 0");
        responseHeaders.add("");
        responseHeaders.add("");

        outputStream.write(
                String.join("\r\n", responseHeaders)
                        .getBytes(StandardCharsets.UTF_8)
        );
        outputStream.flush();
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
