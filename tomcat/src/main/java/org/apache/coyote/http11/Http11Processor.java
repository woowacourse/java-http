package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Session;
import org.apache.catalina.SessionManager;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.controller.RequestMapping;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.net.Socket;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;
    private final RequestMapping requestMapping;

    public Http11Processor(final Socket connection) {
        this(
                connection,
                new RequestMapping()
        );
    }

    public Http11Processor(final Socket connection, final RequestMapping requestMapping) {
        this.connection = connection;
        this.requestMapping = requestMapping;
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

            final Map<String, List<String>> headers = resolveHeader(reader);

            final HttpCookie cookie = new HttpCookie(headers.get("cookie"));
            final boolean shouldIssueJSessionId = !cookie.contains("JSESSIONID");
            final String jSessionId;
            SessionManager sessionManager = SessionManager.getInstance();

            if (cookie.contains("JSESSIONID")) {
                jSessionId = cookie.get("JSESSIONID");
            } else {
                jSessionId = UUID.randomUUID().toString();
            }

            Session session = sessionManager.findSession(jSessionId);

            if (session == null) {
                session = new Session(jSessionId);
                sessionManager.add(session);
            }

            final String method = extractMethod(requestLine);
            final String uri = extractUri(requestLine);
            final String path = extractPath(uri);

            if (handleRoute(
                    method,
                    path,
                    reader,
                    headers,
                    outputStream,
                    jSessionId,
                    shouldIssueJSessionId,
                    session
            )) {
                return;
            }

            final String responsePath = resolveResourcePath(path);
            final String responseBody = createResponseBody(path, responsePath);

            final String response = createResponse(uri, responseBody, jSessionId, shouldIssueJSessionId);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();

        } catch (IOException | UncheckedIOException | UncheckedServletException e ) {
            log.error(e.getMessage(), e);
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }

    private boolean handleRoute(
            final String method,
            final String path,
            final BufferedReader reader,
            final Map<String, List<String>> headers,
            final OutputStream outputStream,
            final String jSessionId,
            final boolean shouldIssueJSessionId,
            final Session session
    ) throws IOException {

        if (isLoginPath(path)) {
            return handleLoginRoute(
                    method,
                    reader,
                    headers,
                    outputStream,
                    jSessionId,
                    shouldIssueJSessionId,
                    session
            );
        }

        if (isRegisterPath(path) && "POST".equals(method)) {
            handleRegister(
                    reader,
                    headers,
                    outputStream,
                    jSessionId,
                    shouldIssueJSessionId
            );
            return true;
        }

        return false;
    }

    private boolean handleLoginRoute(
            final String method,
            final BufferedReader reader,
            final Map<String, List<String>> headers,
            final OutputStream outputStream,
            final String jSessionId,
            final boolean shouldIssueJSessionId,
            final Session session
    ) throws IOException {

        if ("POST".equals(method)) {
            handleLogin(
                    reader,
                    headers,
                    outputStream,
                    jSessionId,
                    shouldIssueJSessionId,
                    session
            );
            return true;
        }

        if ("GET".equals(method)
                && session.getAttribute("user") != null) {

            final String response = createRedirectResponse(
                    "/index.html",
                    jSessionId,
                    shouldIssueJSessionId
            );

            outputStream.write(
                    response.getBytes(StandardCharsets.UTF_8)
            );
            outputStream.flush();

            return true;
        }

        return false;
    }

    private void handleRegister(
            final BufferedReader reader,
            final Map<String, List<String>> headers,
            final OutputStream outputStream,
            final String jSessionId,
            final boolean shouldIssueJSessionId
    ) throws IOException {

        final int bodyLength = resolveContentLength(headers);

        final char[] buffer = new char[bodyLength];
        reader.read(buffer, 0, bodyLength);

        final String requestBody = new String(buffer);
        final Map<String, String> bodyParams = extractBody(requestBody);

        final User newUser = new User(
                bodyParams.get("account"),
                bodyParams.get("password"),
                bodyParams.get("email")
        );

        InMemoryUserRepository.save(newUser);

        final String response = createRedirectResponse(
                "/index.html",
                jSessionId,
                shouldIssueJSessionId
        );

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private Map<String, String> extractBody(final String requestBody) {
        final Map<String, String> bodyParams = new HashMap<>();

        for (String parameter : requestBody.split("&")) {
            final String[] keyValue = parameter.split("=", 2);

            if (keyValue.length == 2) {
                final String key = URLDecoder.decode(
                        keyValue[0],
                        StandardCharsets.UTF_8
                );

                final String value = URLDecoder.decode(
                        keyValue[1],
                        StandardCharsets.UTF_8
                );

                bodyParams.put(key, value);
            }
        }

        return bodyParams;
    }

    private Map<String, List<String>> resolveHeader(
            final BufferedReader reader
    ) {
        final Map<String, List<String>> headers = new HashMap<>();

        try {
            String line = reader.readLine();

            while (line != null && !line.isEmpty()) {
                final String[] header = line.split(":", 2);

                if (header.length != 2) {
                    throw new IllegalArgumentException(
                            "Invalid header line: " + line
                    );
                }

                final String name = header[0]
                        .trim()
                        .toLowerCase(Locale.ROOT);

                final String value = header[1].trim();

                headers.computeIfAbsent(name, key -> new ArrayList<>())
                        .add(value);

                line = reader.readLine();
            }

            return headers;
        } catch (IOException e) {
            throw new UncheckedIOException(
                    "HTTP Header를 읽는 중 오류가 발생했습니다.",
                    e
            );
        }
    }

    private void handleLogin(
            final BufferedReader reader,
            final Map<String, List<String>> headers,
            final OutputStream outputStream,
            final String jSessionId,
            final boolean shouldIssueJSessionId,
            final Session session
    ) throws IOException {
        final int bodyLength = resolveContentLength(headers);

        final char[] buffer = new char[bodyLength];
        reader.read(buffer, 0, bodyLength);

        final String requestBody = new String(buffer);
        final Map<String, String> bodyParams = extractBody(requestBody);

        final String account = bodyParams.get("account");
        final String password = bodyParams.get("password");

        final Optional<User> user = InMemoryUserRepository.findByAccount(account)
                .filter(it -> it.checkPassword(password));

        if (user.isPresent()) {
            session.setAttribute("user", user.get());

            final String response = createRedirectResponse(
                    "/index.html",
                    jSessionId,
                    shouldIssueJSessionId
            );

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
            return;
        }

        final String response = createRedirectResponse(
                "/401.html",
                jSessionId,
                shouldIssueJSessionId
        );

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }

    private int resolveContentLength(Map<String, List<String>> headers) {
        final List<String> headerValues = headers.get("content-length");

        if (headerValues == null || headerValues.isEmpty()) {
            return 0;
        }

        String contentLength = null;

        for (String headerValue : headerValues) {
            for(String value : headerValue.split(",")) {
                final String trimmedValue = value.trim();

                if (contentLength == null) {
                    contentLength = trimmedValue;
                    continue;
                }

                if (!contentLength.equals(trimmedValue)) {
                    throw new IllegalArgumentException("conflicting header value: " + headerValue);
                }
            }
        }

        if (contentLength == null) {
            return 0;
        }

        try {
            final int length = Integer.parseInt(contentLength);

            if (length < 0) {
                throw new IllegalArgumentException("Invalid Content-Length: " + contentLength);
            }

            return length;
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid Content-Length: " + contentLength, e);
        }
    }

    private String extractMethod(final String requestLine) {
        return requestLine.split(" ")[0];
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

    private String resolveResourcePath(final String path) {
        if (isLoginPath(path)) {
            return "/login.html";
        }

        if (isRegisterPath(path)) {
            return "/register.html";
        }

        return path;
    }

    private boolean isLoginPath(final String path) {
        return "/login".equals(path);
    }

    private boolean isRegisterPath(final String path) {
        return "/register".equals(path);
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
            final String responseBody,
            final String jSessionId,
            final boolean shouldIssueJSessionId
    ) {
        final String contentType = resolveContentType(uri);
        final byte[] body = responseBody.getBytes(StandardCharsets.UTF_8);

        if (shouldIssueJSessionId) {
            return String.join(
                    "\r\n",
                    "HTTP/1.1 200 OK",
                    "Set-Cookie: JSESSIONID=" + jSessionId,
                    "Content-Type: " + contentType,
                    "Content-Length: " + body.length,
                    "",
                    responseBody
            );
        }

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
            return "text/javascript";
        }

        return "text/html;charset=utf-8";
    }

    private String createRedirectResponse(
            final String location,
            final String jSessionId,
            final boolean shouldIssueJSessionId
    ) {

        if (shouldIssueJSessionId) {
            return String.join(
                    "\r\n",
                    "HTTP/1.1 302 Found",
                    "Set-Cookie: JSESSIONID=" + jSessionId,
                    "Location: " + location,
                    "Content-Length: 0",
                    "",
                    ""
            );
        }
        return String.join(
                "\r\n",
                "HTTP/1.1 302 Found",
                "Location: " + location,
                "Content-Length: 0",
                "",
                ""
        );
    }
}
