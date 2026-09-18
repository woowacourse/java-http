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
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    private final StaticResourceLoader resourceLoader = new StaticResourceLoader();

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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));

            String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            String[] tokens = requestLine.split(" ");
            if (tokens.length < 2) {
                return;
            }

            String method = tokens[0];
            var requestUri = new RequestUri(tokens[1]);

            Map<String, String> headers = readHeaders(reader);
            String body = readBody(reader, headers);
            Optional<String> newSessionId = new HttpCookie(headers.get("cookie"))
                    .createJSessionIdIfAbsent();

            handleRequest(method, requestUri, body, newSessionId, outputStream);
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] pair = line.split(":", 2);

            if (pair.length == 2) {
                headers.put(pair[0].trim().toLowerCase(Locale.ROOT), pair[1].trim());
            }
        }

        return headers;
    }

    private String readBody(
            BufferedReader reader,
            Map<String, String> headers
    ) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int readCount = reader.read(
                    buffer,
                    totalRead,
                    contentLength - totalRead
            );

            if (readCount == -1) {
                throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
            }

            totalRead += readCount;
        }

        return new String(buffer);
    }

    private void handleRequest(
            String method,
            RequestUri requestUri,
            String body,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        String path = requestUri.getPath();
        switch (path) {
            case "/" -> writeResponse(outputStream, "200 OK", "Hello world!", "text/html", newSessionId);
            case "/register" -> handleRegister(method, body, newSessionId, outputStream);
            case "/login" -> handleLogin(method, body, newSessionId, outputStream);
            default -> serveResource(path, newSessionId, outputStream);
        }
    }

    private void handleRegister(
            String method,
            String body,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        if (method.equals("GET")) {
            serveResource("/register.html", newSessionId, outputStream);
            return;
        }

        if (method.equals("POST")) {
            Map<String, String> parameters = parseFormBody(body);
            InMemoryUserRepository.save(new User(
                    parameters.get("account"),
                    parameters.get("password"),
                    parameters.get("email")
            ));

            writeRedirect(outputStream, "/index.html", newSessionId);
        }
    }

    private void handleLogin(
            String method,
            String body,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        if (method.equals("GET")) {
            serveResource("/login.html", newSessionId, outputStream);
            return;
        }

        if (method.equals("POST")) {
            Map<String, String> parameters = parseFormBody(body);
            Optional<User> loginUser = login(parameters);
            if (loginUser.isEmpty()) {
                writeRedirect(outputStream, "/401.html", newSessionId);
                return;
            }

            User user = loginUser.get();
            log.info("회원 조회 성공: account={}", user.getAccount());
            writeRedirect(outputStream, "/index.html", newSessionId);
        }
    }

    private Map<String, String> parseFormBody(String body) {
        Map<String, String> parameters = new HashMap<>();

        for (String parameter : body.split("&")) {
            String[] pair = parameter.split("=", 2);
            if (pair.length != 2) {
                continue;
            }

            String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            parameters.put(name, value);
        }

        return parameters;
    }

    private void serveResource(
            String path,
            Optional<String> newSessionId,
            OutputStream outputStream
    ) throws IOException {
        String responseBody;
        try {
            responseBody = resourceLoader.load(path);
        } catch (FileNotFoundException e) {
            writeResponse(outputStream, "404 Not Found", "Not Found", "text/plain", newSessionId);
            return;
        }

        String contentType = path.endsWith(".css") ? "text/css" : "text/html";
        writeResponse(outputStream, "200 OK", responseBody, contentType, newSessionId);
    }

    private Optional<User> login(Map<String, String> params) {
        String account = params.get("account");
        String password = params.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void writeRedirect(
            OutputStream outputStream,
            String location,
            Optional<String> newSessionId
    ) throws IOException {
        String response = "HTTP/1.1 302 Found\r\n"
                + setCookieHeader(newSessionId)
                + "Location: " + location + "\r\n"
                + "Content-Length: 0\r\n\r\n";
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private void writeResponse(
            OutputStream outputStream,
            String status,
            String responseBody,
            String contentType,
            Optional<String> newSessionId
    ) throws IOException {
        byte[] responseBodyBytes = responseBody.getBytes(StandardCharsets.UTF_8);

        String response = "HTTP/1.1 " + status + " \r\n"
                + setCookieHeader(newSessionId)
                + "Content-Type: " + contentType + ";charset=utf-8 \r\n"
                + "Content-Length: " + responseBodyBytes.length + " \r\n"
                + "\r\n"
                + responseBody;
        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private String setCookieHeader(Optional<String> newSessionId) {
        return newSessionId
                .map(sessionId -> "Set-Cookie: " + HttpCookie.JSESSION_ID + "=" + sessionId + "\r\n")
                .orElse("");
    }
}
