package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import org.apache.coyote.Processor;
import org.apache.coyote.http11.session.Session;
import org.apache.coyote.http11.session.SessionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.net.Socket;
import java.net.URLDecoder;

public class Http11Processor implements Runnable, Processor {

    private static final Logger log = LoggerFactory.getLogger(Http11Processor.class);

    private final Socket connection;

    /**
     * 단일 요청 처리 진입점
     *
     * @param connection
     */
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

            BufferedReader bufferedReader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );
            final String requestLine = bufferedReader.readLine();

            final String[] requestParts = requestLine.split(" ");

            final String method = requestParts[0];
            final String requestTarget = requestParts[1];
            final String httpVersion = requestParts[2];

            final String requestUri = requestTarget.split("\\?", 2)[0];

            /**
             * header
             */
            final Map<String, String> requestHeaders = readRequestHeaders(bufferedReader);
            final int contentLength = parseContentLength(requestHeaders);
            final String cookieHeader = requestHeaders.get("cookie");
            final String requestSessionId = Cookie.getValue(cookieHeader, "JSESSIONID");
            final Session existingSession = SessionManager.findSession(requestSessionId);
            final Session session = existingSession != null
                    ? existingSession
                    : SessionManager.createSession();
            final String sessionCookie = existingSession == null
                    ? "JSESSIONID=" + session.getId()
                    : null;

            /**
             * body
             */
            final String requestBody = readRequestBody(bufferedReader, contentLength);

            final String responseBody;
            if ("/".equals(requestUri)) {
                responseBody = "Hello world!";
            } else {
                final String resourcePath;

                if ("/login".equals(requestUri)) {
                    if ("GET".equalsIgnoreCase(method)
                            && session.getAttribute("user") != null) {
                        writeRedirectResponse(outputStream, "/index.html", sessionCookie);
                        return;
                    }

                    if ("POST".equalsIgnoreCase(method)) {
                        final Map<String, String> queryParams = parseQueryString(requestBody);

                        final String account = queryParams.get("account");
                        final String password = queryParams.get("password");

                        final Optional<User> optionalUser = account == null
                                ? Optional.empty()
                                : InMemoryUserRepository.findByAccount(account);
                        final boolean loginSuccess = password != null
                                && optionalUser
                                .map(user -> user.checkPassword(password))
                                .orElse(false);

                        if (loginSuccess) {
                            final User user = optionalUser.orElseThrow();
                            session.setAttribute("user", user);
                            log.info("회원 조회 결과: {}", user);
                        }

                        writeRedirectResponse(
                                outputStream,
                                loginSuccess ? "/index.html" : "/401.html",
                                sessionCookie
                        );
                        return;
                    }

                    resourcePath = "/login.html";
                } else if ("/register".equals(requestUri)) {
                    if ("POST".equalsIgnoreCase(method)) {
                        final Map<String, String> formData = parseQueryString(requestBody);
                        final String account = formData.get("account");
                        final String password = formData.get("password");
                        final String email = formData.get("email");

                        if (account != null && password != null && email != null) {
                            final User user = new User(account, password, email);
                            InMemoryUserRepository.save(user);
                            log.info("회원가입 결과: {}", user);
                        }

                        writeRedirectResponse(outputStream, "/index.html", sessionCookie);
                        return;
                    }

                    resourcePath = "/register.html";
                } else {
                    resourcePath = requestUri;
                }

                final var resource = getClass().getClassLoader()
                        .getResource("static" + resourcePath);

                if (resource == null) {
                    return;
                }

                try {
                    final byte[] body =
                            Files.readAllBytes(Path.of(resource.toURI()));

                    responseBody = new String(body, StandardCharsets.UTF_8);
                } catch (URISyntaxException e) {
                    throw new IllegalStateException(e);
                }
            }

            final String contentType;
            if (requestUri.endsWith(".css")) {
                contentType = "text/css";
            } else {
                contentType = "text/html;charset=utf-8";
            }

            final List<String> responseHeaders = new ArrayList<>();
            responseHeaders.add("HTTP/1.1 200 OK ");

            if (sessionCookie != null) {
                responseHeaders.add("Set-Cookie: " + sessionCookie + " ");
            }

            responseHeaders.add("Content-Type: " + contentType + " ");
            responseHeaders.add("Content-Length: "
                    + responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
            responseHeaders.add("");
            responseHeaders.add(responseBody);

            final String response = String.join("\r\n", responseHeaders);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private String readRequestBody(final BufferedReader bufferedReader,
                                   final int contentLength) throws IOException {
        if (contentLength == 0) {
            return "";
        }

        final char[] body = new char[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            final int read = bufferedReader.read(body, offset, contentLength - offset);
            if (read == -1) {
                break;
            }
            offset += read;
        }

        return new String(body, 0, offset);
    }

    private Map<String, String> readRequestHeaders(final BufferedReader bufferedReader)
            throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = bufferedReader.readLine()) != null && !line.isEmpty()) {
            final int separatorIndex = line.indexOf(':');
            if (separatorIndex <= 0) {
                continue;
            }

            final String name = line.substring(0, separatorIndex)
                    .trim()
                    .toLowerCase(Locale.ROOT);
            final String value = line.substring(separatorIndex + 1).trim();
            headers.put(name, value);
        }

        return headers;
    }

    private int parseContentLength(final Map<String, String> headers) {
        final String contentLength = headers.get("content-length");
        if (contentLength == null || contentLength.isBlank()) {
            return 0;
        }

        return Integer.parseInt(contentLength);
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> queryParams = new HashMap<>();

        if (queryString.isBlank()) {
            return queryParams;
        }

        for (String parameter : queryString.split("&")) {
            final String[] keyValue = parameter.split("=", 2);

            if (keyValue.length != 2) {
                continue;
            }

            final String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
            final String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);

            queryParams.put(key, value);
        }

        return queryParams;
    }

    private void writeRedirectResponse(final OutputStream outputStream,
                                       final String location,
                                       final String sessionCookie) throws IOException {
        final List<String> responseHeaders = new ArrayList<>();
        responseHeaders.add("HTTP/1.1 302 Found");

        if (sessionCookie != null) {
            responseHeaders.add("Set-Cookie: " + sessionCookie);
        }

        responseHeaders.add("Location: " + location);
        responseHeaders.add("Content-Length: 0");
        responseHeaders.add("");
        responseHeaders.add("");

        final String response = String.join("\r\n", responseHeaders);

        outputStream.write(response.getBytes(StandardCharsets.UTF_8));
        outputStream.flush();
    }
}
