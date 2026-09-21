package org.apache.coyote.http11;

import com.techcourse.db.InMemoryUserRepository;
import com.techcourse.exception.UncheckedServletException;
import com.techcourse.model.User;
import org.apache.coyote.Processor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
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

            final var reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            final String requestLine = reader.readLine();
            if (requestLine == null) {
                return;
            }

            final String[] requestParts = requestLine.split(" ", 3);
            final String method = requestParts[0];
            final String requestUri = requestParts[1];
            final Map<String, String> headers = readHeaders(reader);
            final String requestBody = readRequestBody(reader, headers);
            final HttpCookie cookies = HttpCookie.parse(headers.get("Cookie"));
            final String setCookie = cookies.get(HttpCookie.JSESSIONID)
                    .isPresent() ? null : HttpCookie.newJSessionId();
            final Optional<String> redirectLocation = resolveRedirect(method, requestUri, requestBody);
            if (redirectLocation.isPresent()) {
                final String response = redirectResponse(redirectLocation.get(), setCookie);
                outputStream.write(response.getBytes(StandardCharsets.UTF_8));
                outputStream.flush();
                return;
            }

            final String path = resolvePath(requestUri);

            String responseBody = "Hello world!";
            if (!"/".equals(path)) {
                final var resource = getClass().getClassLoader().getResource("static" + path);
                if (resource != null) {
                    responseBody = Files.readString(new File(resource.getFile()).toPath(), StandardCharsets.UTF_8);
                }
            }

            String contentType = "text/html";
            if (path.endsWith(".css")) {
                contentType = "text/css";
            }
            final var responseHeaders = new ArrayList<String>();
            responseHeaders.add("HTTP/1.1 200 OK ");
            responseHeaders.add("Content-Type: " + contentType + ";charset=utf-8 ");
            addSetCookieHeader(responseHeaders, setCookie);
            responseHeaders.add("Content-Length: " + responseBody.getBytes(StandardCharsets.UTF_8).length + " ");
            responseHeaders.add("");
            responseHeaders.add(responseBody);
            final var response = String.join("\r\n", responseHeaders);

            outputStream.write(response.getBytes(StandardCharsets.UTF_8));
            outputStream.flush();
        } catch (IOException | UncheckedServletException e) {
            log.error(e.getMessage(), e);
        }
    }

    private Optional<String> resolveRedirect(final String method, final String requestUri, final String requestBody) {
        final int queryIndex = requestUri.indexOf('?');
        final String path = queryIndex >= 0 ? requestUri.substring(0, queryIndex) : requestUri;

        if ("/login".equals(path)) {
            if ("POST".equals(method)) {
                return Optional.of(login(requestBody)
                        ? "/index.html"
                        : "/401.html");
            }
        }

        if ("/register".equals(path) && "POST".equals(method)) {
            register(requestBody);
            return Optional.of("/index.html");
        }

        return Optional.empty();
    }

    private String redirectResponse(final String location, final String setCookie) {
        final var responseHeaders = new ArrayList<String>();
        responseHeaders.add("HTTP/1.1 302 Found ");
        responseHeaders.add("Location: " + location + " ");
        addSetCookieHeader(responseHeaders, setCookie);
        responseHeaders.add("Content-Length: 0 ");
        responseHeaders.add("");
        responseHeaders.add("");
        return String.join("\r\n", responseHeaders);
    }

    private void addSetCookieHeader(final ArrayList<String> responseHeaders, final String setCookie) {
        if (setCookie != null) {
            responseHeaders.add("Set-Cookie: " + setCookie + " ");
        }
    }

    private String resolvePath(final String requestUri) {
        final int queryIndex = requestUri.indexOf('?');
        String path = requestUri;
        if (queryIndex >= 0) {
            path = requestUri.substring(0, queryIndex);
        }

        if (!"/login".equals(path)) {
            return path;
        }

        return "/login.html";
    }

    private Map<String, String> readHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String header;
        while ((header = reader.readLine()) != null && !header.isEmpty()) {
            final int separator = header.indexOf(':');
            if (separator < 0) {
                continue;
            }
            headers.put(header.substring(0, separator), header.substring(separator + 1).trim());
        }
        return headers;
    }

    private String readRequestBody(final BufferedReader reader, final Map<String, String> headers) throws IOException {
        final String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) {
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        int offset = 0;
        while (offset < contentLength) {
            final int read = reader.read(buffer, offset, contentLength - offset);
            if (read < 0) {
                break;
            }
            offset += read;
        }
        return new String(buffer, 0, offset);
    }

    private boolean login(final String queryString) {
        final Map<String, String> parameters = parseQueryString(queryString);
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        if (account == null || password == null) {
            return false;
        }

        return InMemoryUserRepository.findByAccount(account)
                .filter(user -> user.checkPassword(password))
                .map(user -> {
                    log.info("Login succeeded: account={}", user.getAccount());
                    return true;
                })
                .orElse(false);
    }

    private void register(final String requestBody) {
        final Map<String, String> parameters = parseQueryString(requestBody);
        final String account = parameters.get("account");
        final String password = parameters.get("password");
        final String email = parameters.get("email");
        if (account == null || password == null || email == null) {
            return;
        }

        InMemoryUserRepository.save(new User(account, password, email));
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> parameters = new HashMap<>();
        for (String parameter : queryString.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                parameters.put(
                        URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return parameters;
    }
}
