package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.model.LoginService;
import com.techcourse.model.RegisterService;
import jakarta.servlet.http.HttpSession;
import org.apache.catalina.SimpleHttpSession;
import org.apache.catalina.SimpleManager;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private static final SimpleManager sessionManager = new SimpleManager();

    private final Socket connection;
    private final LoginService loginService = new LoginService();
    private final RegisterService registerService = new RegisterService();

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
             final var reader = new BufferedReader(new InputStreamReader(inputStream))) {

            final var request = parseRequest(reader);
            if (request == null) {
                return;
            }

            final var method = request.get("method");
            final var path = request.get("path");
            final var params = parseQueryString(request.get("params"));
            final var cookieHeader = request.get("cookie");

            final var cookie = RequestCookie.from(cookieHeader);
            final var extraHeaders = new LinkedHashMap<String, List<String>>();

            HttpSession session;
            if (cookie.contains("JSESSIONID")) {
                final var sessionId = cookie.get("JSESSIONID");
                session = sessionManager.findSession(sessionId);
                if (session == null) {
                    session = SimpleHttpSession.ofGeneratedId();
                    sessionManager.add(session);
                    extraHeaders.put("Set-Cookie", List.of("JSESSIONID=" + session.getId()));
                }
            } else {
                session = SimpleHttpSession.ofGeneratedId();
                sessionManager.add(session);
                extraHeaders.put("Set-Cookie", List.of("JSESSIONID=" + session.getId()));
            }

            if ("/login".equals(path)) {
                final var sessionUser = session.getAttribute("user");

                if ("GET".equalsIgnoreCase(method)) {
                    if (sessionUser != null) {
                        extraHeaders.put("Location", List.of("/index.html"));
                        writeResponse(outputStream, 302, "Found", extraHeaders, null, "text/html;charset=utf-8");
                        return;
                    }

                    final var resourceUrl = Objects.requireNonNull(getClass().getClassLoader()
                            .getResource("static/login.html"));
                    final var body = readResourceFile(resourceUrl);

                    writeResponse(outputStream, 200, "OK", extraHeaders, body, "text/html;charset=utf-8");
                    return;
                }

                if ("POST".equalsIgnoreCase(method)) {
                    final var account = params.get("account");
                    final var password = params.get("password");

                    final var user = InMemoryUserRepository.findByAccount(account)
                            .filter(u -> u.checkPassword(password))
                            .orElse(null);

                    if (user != null) {
                        session.setAttribute("user", user);
                        extraHeaders.put("Location", List.of("/index.html"));
                        writeResponse(outputStream, 302, "Found", extraHeaders, null, "text/html;charset=utf-8");
                        return;
                    }

                    extraHeaders.put("Location", List.of("/401.html"));
                    writeResponse(outputStream, 302, "Found", extraHeaders, null, "text/html;charset=utf-8");
                    return;
                }
            }

            if ("/register".equals(path)) {
                if ("GET".equalsIgnoreCase(method)) {
                    final var resourceUrl = Objects.requireNonNull(getClass().getClassLoader()
                            .getResource("static/register.html"));
                    final var body = readResourceFile(resourceUrl);

                    writeResponse(outputStream, 200, "OK", extraHeaders, body, "text/html;charset=utf-8");
                    return;
                }

                if ("POST".equalsIgnoreCase(method)) {
                    final var account = params.get("account");
                    final var password = params.get("password");
                    final var email = params.get("email");

                    registerService.register(account, password, email);

                    extraHeaders.put("Location", List.of("/index.html"));
                    writeResponse(outputStream, 302, "Found", extraHeaders, null, "text/html;charset=utf-8");
                    return;
                }
            }

            final var staticResourcePath = "static" + path;
            final var staticResourceUrl = getClass().getClassLoader()
                    .getResource(staticResourcePath);

            if (staticResourceUrl != null && !Files.isDirectory(Path.of(staticResourceUrl.toURI()))) {
                final var body = readResourceFile(staticResourceUrl);
                final var contentType = detectContentType(staticResourceUrl);

                writeResponse(outputStream, 200, "OK", extraHeaders, body, contentType);
                return;
            }

            final var body = "Hello world!".getBytes(StandardCharsets.UTF_8);
            writeResponse(outputStream, 200, "OK", extraHeaders, body, "text/html;charset=utf-8");
        } catch (Exception e) {
            log.error("Internal Server Error: {}", e.getMessage(), e);
            sendInternalServerErrorResponse(connection);
        }
    }

    private Map<String, String> parseRequest(final BufferedReader reader) throws IOException {
        final var requestLine = reader.readLine();
        if (requestLine == null || requestLine.isBlank()) {
            return null;
        }

        final var methodAndUri = extractMethodAndUri(requestLine);
        final var method = methodAndUri.get("method");
        final var path = methodAndUri.get("path");
        final var query = methodAndUri.get("query");

        final var headers = new HashMap<String, String>();
        String line;
        while (!(line = reader.readLine()).isBlank()) {
            final var headerParts = line.split(":", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0].trim(), headerParts[1].trim());
            }
        }

        final var allParams = new StringBuilder();
        if (query != null) {
            allParams.append(query);
        }

        if ("POST".equalsIgnoreCase(method)) {
            final var contentLength = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            if (contentLength > 0) {
                final var bodyChars = new char[contentLength];
                reader.read(bodyChars, 0, contentLength);
                if (!allParams.isEmpty()) {
                    allParams.append("&");
                }
                allParams.append(new String(bodyChars));
            }
        }

        final var result = new HashMap<String, String>();
        result.put("method", method);
        result.put("path", path);
        result.put("params", allParams.toString());
        result.put("cookie", headers.get("Cookie"));

        return result;
    }

    private Map<String, String> extractMethodAndUri(final String requestLine) {
        final var result = new HashMap<String, String>();
        final var parts = requestLine.split(" ");
        final var method = parts[0];
        final var uri = parts[1];

        result.put("method", method);

        final var index = uri.indexOf("?");
        if (index != -1) {
            result.put("path", uri.substring(0, index));
            result.put("query", uri.substring(index + 1));
        } else {
            result.put("path", uri);
            result.put("query", null);
        }

        return result;
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final var paramMap = new HashMap<String, String>();

        if (queryString == null || queryString.isBlank()) {
            return paramMap;
        }

        final var queries = queryString.split("&");
        for (final var query : queries) {
            final var keyValue = query.split("=", 2);
            if (keyValue.length == 2) {
                paramMap.put(keyValue[0], keyValue[1]);
            }
        }

        return paramMap;
    }

    private byte[] readResourceFile(final URL resourceUrl) throws IOException, URISyntaxException {
        final var path = Path.of(resourceUrl.toURI());
        return Files.readAllBytes(path);
    }

    private String detectContentType(final URL resourceUrl) throws IOException, URISyntaxException {
        final var path = Path.of(resourceUrl.toURI());
        final var contentType = Files.probeContentType(path);
        return contentType != null ? contentType : "text/plain;charset=utf-8";
    }

    private void writeResponse(
            final OutputStream outputStream,
            final int statusCode,
            final String statusMessage,
            final Map<String, List<String>> extraHeaders,
            final byte[] body,
            final String contentType
    ) throws IOException {
        final var headers = new StringBuilder()
                .append("HTTP/1.1 ")
                .append(statusCode)
                .append(" ")
                .append(statusMessage)
                .append("\r\n")
                .append("Content-Type: ")
                .append(contentType)
                .append("\r\n");

        if (body != null) {
            headers.append("Content-Length: ")
                    .append(body.length)
                    .append("\r\n");
        }

        if (extraHeaders != null) {
            extraHeaders.forEach((k, values) -> values.forEach(v ->
                    headers.append(k)
                            .append(": ")
                            .append(v)
                            .append("\r\n")));
        }

        headers.append("\r\n");
        outputStream.write(headers.toString()
                .getBytes(StandardCharsets.UTF_8));

        if (body != null) {
            outputStream.write(body);
        }

        outputStream.flush();
    }

    private void sendInternalServerErrorResponse(final Socket connection) {
        try {
            final var resourceUrl = getClass().getClassLoader()
                    .getResource("static/500.html");
            final var body = (resourceUrl != null)
                    ? readResourceFile(resourceUrl)
                    : "Internal Server Error".getBytes(StandardCharsets.UTF_8);

            writeResponse(
                    connection.getOutputStream(),
                    500,
                    "Internal Server Error",
                    Map.of(),
                    body,
                    "text/html;charset=utf-8"
            );
        } catch (Exception ex) {
            log.error("Failed to send {} response: {}", 500, ex.getMessage(), ex);
        }
    }
}
