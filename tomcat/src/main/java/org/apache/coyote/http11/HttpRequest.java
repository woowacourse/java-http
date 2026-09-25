package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters = new HashMap<>();

    private final String body;
    private final HttpCookie cookies;
    private Session session;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.cookies = HttpCookie.parse(headers.get("Cookie"));
        parseParameters(requestLine.query());
        final String contentType = headers.get("Content-Type");

        if (contentType == null || contentType.split(";", 2)[0].trim()
                .equalsIgnoreCase("application/x-www-form-urlencoded")) {
            parseParameters(body);
        }
    }

    public static Optional<HttpRequest> read(final InputStream input) throws IOException {
        final String line = readLine(input);
        if (line == null) {
            return Optional.empty();
        }
        try {
            final RequestLine requestLine = RequestLine.parse(line);
            final Map<String, String> headers = readHeaders(input);
            if (headers.containsKey("Transfer-Encoding")) {
                throw new IOException("Transfer-Encoding is not supported");
            }
            final int length = Integer.parseInt(headers.getOrDefault("Content-Length", "0"));
            if (length < 0) {
                throw new IOException("Negative Content-Length");
            }
            final byte[] bytes = input.readNBytes(length);
            if (bytes.length != length) {
                throw new EOFException("Incomplete request body");
            }
            return Optional.of(new HttpRequest(requestLine, headers, new String(bytes, StandardCharsets.UTF_8)));
        } catch (IllegalArgumentException e) {
            throw new IOException("Invalid HTTP request", e);
        }
    }

    private static Map<String, String> readHeaders(final InputStream input) throws IOException {
        final Map<String, String> headers = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
        String line;
        while ((line = readLine(input)) != null) {
            if (line.isEmpty()) {
                return headers;
            }
            final int separator = line.indexOf(':');
            if (separator <= 0) {
                throw new IOException("Invalid HTTP header");
            }
            final String name = line.substring(0, separator);
            if (name.equalsIgnoreCase("Content-Length") && headers.containsKey(name)) {
                throw new IOException("Duplicate Content-Length");
            }
            headers.put(name, line.substring(separator + 1).trim());
        }
        throw new EOFException("Incomplete request headers");
    }

    private static String readLine(final InputStream input) throws IOException {
        final var bytes = new ByteArrayOutputStream();
        int value;
        while ((value = input.read()) != -1) {
            if (value == '\n') {
                final String line = bytes.toString(StandardCharsets.ISO_8859_1);
                return line.endsWith("\r") ? line.substring(0, line.length() - 1) : line;
            }
            bytes.write(value);
        }
        if (bytes.size() > 0) {
            throw new EOFException("Incomplete HTTP line");
        }
        return null;
    }

    private void parseParameters(final String encoded) {
        if (encoded.isEmpty()) {
            return;
        }
        for (String parameter : encoded.split("&")) {
            final String[] pair = parameter.split("=", 2);
            parameters.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                    pair.length == 2 ? URLDecoder.decode(pair[1], StandardCharsets.UTF_8) : "");
        }
    }

    public String getMethod() {
        return requestLine.method();
    }

    public String getPath() {
        return requestLine.path();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public String getBody() {
        return body;
    }

    public Optional<String> getCookie(final String name) {
        return cookies.get(name);
    }

    public Session getSession() {
        if (session == null) {
            throw new IllegalStateException("Session has not been initialized");
        }
        return session;
    }

    void initializeSession(final SessionManager manager) {
        final Optional<String> sessionId = getCookie(HttpCookie.JSESSIONID);
        session = manager.getOrCreate(sessionId.orElseGet(HttpCookie::newSessionId));
        sessionId.ifPresent(ignored -> session.markAsJoined());
    }
}
