package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import jakarta.servlet.http.HttpSession;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
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
                final var input = new BufferedInputStream(connection.getInputStream());
                final var outputStream = connection.getOutputStream()) {

            String requestLine = readLine(input);
            String[] splitRequestLine = requestLine.split(" ");

            String method = splitRequestLine[0];
            String requestTarget = splitRequestLine[1];
            String protocol = splitRequestLine[2];

            Map<String, String> headers = readHeaders(input);
            HttpCookie cookie = new HttpCookie(headers.get("cookie"));
            String sessionId = cookie.get("JSESSIONID");

            Map<String, String> responseHeaders = new HashMap<>();
            if (sessionId == null) {
                sessionId = UUID.randomUUID().toString();
                responseHeaders.put("Set-Cookie", "JSESSIONID=" + sessionId);
            }

            String requestBody = readRequestBody(headers, input);

            if (method.equals("GET")) {
                handleGetRequest(outputStream, requestTarget, sessionId, responseHeaders);
            } else if (method.equals("POST")) {
                handlePostRequest(outputStream, requestTarget, headers, sessionId, requestBody, responseHeaders);
            }

        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private static String readLine(InputStream input) throws IOException {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();

        int value;
        while ((value = input.read()) != -1 && value != '\n') {
            if (value != '\r') {
                bytes.write(value);
            }
        }

        return bytes.toString();
    }

    @Nonnull
    private static String readRequestBody(Map<String, String> headers, BufferedInputStream input) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        byte[] bodyBytes = input.readNBytes(contentLength);
        if (bodyBytes.length != contentLength) {
            throw new EOFException("요청 본문이 중간에 끝났습니다.");
        }

        String requestBody = new String(bodyBytes, StandardCharsets.UTF_8);
        return requestBody;
    }

    @Nonnull
    private static Map<String, String> readHeaders(BufferedInputStream input) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = readLine(input)) != null && !line.isEmpty()) {
            int colonIndex = line.indexOf(":");

            String name = line.substring(0, colonIndex).trim().toLowerCase(Locale.ROOT);
            String value = line.substring(colonIndex + 1).trim();
            headers.put(name, value);
        }
        return headers;
    }

    private void handleGetRequest(OutputStream outputStream, String requestTarget, String sessionId, Map<String, String> responseHeaders) throws IOException {
        // root 처리
        if (requestTarget.equals("/")) {
            responseHeaders.put("Content-Type", "text/html;charset=utf-8 ");

            writeResponse(outputStream,
                    HttpStatus.OK,
                    responseHeaders,
                    "Hello world!".getBytes(StandardCharsets.UTF_8));

            return;
        }

        ParsedTarget parsedTarget = parseRequestTarget(requestTarget);
        String resourceName = parsedTarget.path();

        if (resourceName.equals("/login")) {
            Session session = SessionManager.getInstance().findSession(sessionId);

            if (session != null && session.getAttribute("user") != null) {
                responseHeaders.put("Location", "/index.html");
                writeResponse(outputStream, HttpStatus.FOUND, responseHeaders, new byte[0]);
                return;
            }

            resourceName = "login.html";
        } else if (resourceName.equals("/register")) {
            resourceName = "register.html";
        }

        responseHeaders.put("Content-Type", resolveContentType(resourceName));
        byte[] responseBody = readResponseBody(resourceName);

        writeResponse(
                outputStream,
                HttpStatus.OK,
                responseHeaders,
                responseBody
        );
    }

    private void handlePostRequest(OutputStream outputStream,
                                   String requestTarget,
                                   Map<String, String> headers,
                                   String sessionId,
                                   String requestBody,
                                   Map<String, String> responseHeaders) throws IOException {
        Map<String, String> bodyFields = null;

        if (headers.get("content-type").equals("application/x-www-form-urlencoded")) {
            bodyFields = parseUrlEncodedParameters(requestBody);
        }

        ParsedTarget parsedTarget = parseRequestTarget(requestTarget);
        String path = parsedTarget.path();

        if (path.equals("/register")) {
            handleRegister(bodyFields);
            responseHeaders.put("Location", "/index.html");
            writeResponse(outputStream, HttpStatus.FOUND, responseHeaders, new byte[0]);
            return;
        }

        if (path.equals("/login")) {
            Optional<User> authenticatedUser = authenticate(bodyFields);

            if (authenticatedUser.isPresent()) {
                saveUserInSession(sessionId, authenticatedUser.get());
                responseHeaders.put("Location", "/index.html");
                writeResponse(outputStream, HttpStatus.FOUND, responseHeaders, new byte[0]);
                return;
            }

            byte[] responseBody = readResponseBody("401.html");
            responseHeaders.put("Content-Type", resolveContentType("401.html"));
            writeResponse(outputStream, HttpStatus.UNAUTHORIZED, responseHeaders, responseBody);
        }
    }

    private void handleRegister(Map<String, String> bodyFields) {
        String account = bodyFields.get("account");
        String email = bodyFields.get("email");
        String password = bodyFields.get("password");

        if (account == null || email == null || password == null) {
            return;
        }

        User user = new User(account, password, email);
        InMemoryUserRepository.save(user);
    }

    private ParsedTarget parseRequestTarget(String requestTarget) {
        int queryStartIndex = requestTarget.indexOf("?");

        if (queryStartIndex < 0) {
            return new ParsedTarget(
                    requestTarget,
                    new HashMap<>()
            );
        }

        String path = requestTarget.substring(0, queryStartIndex);
        String queryString = requestTarget.substring(queryStartIndex + 1);

        return new ParsedTarget(path, parseUrlEncodedParameters(queryString));
    }

    private Map<String, String> parseUrlEncodedParameters(String encodedParameters) {
        Map<String, String> parameters = new HashMap<>();

        if (encodedParameters == null || encodedParameters.isEmpty()) {
            return parameters;
        }

        for (String parameter : encodedParameters.split("&")) {
            String[] keyValue = parameter.split("=", 2);

            if (keyValue.length == 2) {
                String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
                String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
                parameters.put(key, value);
            }
        }

        return parameters;
    }

    private static Optional<User> authenticate(Map<String, String> bodyFields) {
        String account = bodyFields.get("account");
        String password = bodyFields.get("password");

        if (account == null || password == null) {
            return Optional.empty();
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password));
    }

    private void saveUserInSession(String sessionId, User user) {
        SessionManager sessionManager = SessionManager.getInstance();
        Session session = sessionManager.findSession(sessionId);

        if (session == null) {
            session = new Session(sessionId);
            sessionManager.add(session);
        }

        session.setAttribute("user", user);
    }

    private static String resolveContentType(String resourceName) {
        if (resourceName.endsWith(".css")) {
            return "text/css;charset=utf-8 ";
        }

        if (resourceName.endsWith(".html")) {
            return "text/html;charset=utf-8 ";
        }

        return "application/octet-stream";
    }

    private byte[] readResponseBody(String resourceName) throws IOException {
        final byte[] responseBody;

        final URL resource = Objects.requireNonNull(
                getClass().getClassLoader()
                        .getResource("static/" + resourceName),
                "해당 리소스를 찾을 수 없습니다."
        );
        final Path path = new File(resource.getFile()).toPath();

        responseBody = Files.readAllBytes(path);
        return responseBody;
    }

    private void writeResponse(
            OutputStream outputStream,
            HttpStatus status,
            Map<String, String> headers,
            byte[] responseBody
    ) throws IOException {
        StringBuilder responseHead = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(status.getCode())
                .append(' ')
                .append(status.getReasonPhrase())
                .append(" \r\n");

        for (Map.Entry<String, String> header : headers.entrySet()) {
            responseHead.append(header.getKey())
                    .append(": ")
                    .append(header.getValue())
                    .append("\r\n");
        }

        responseHead.append("Content-Length: ")
                .append(responseBody.length)
                .append(" \r\n\r\n");

        outputStream.write(responseHead.toString().getBytes(StandardCharsets.UTF_8));
        outputStream.write(responseBody);
        outputStream.flush();
    }
}
