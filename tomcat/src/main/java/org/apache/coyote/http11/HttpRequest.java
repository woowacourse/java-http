package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.apache.catalina.Manager;
import org.apache.catalina.Session;

public final class HttpRequest {
    private static final String EMPTY_LINE = "";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final byte[] body;
    private final Manager sessionManager;
    private Session session;

    private HttpRequest(
            final RequestLine requestLine,
            final Map<String, String> headers,
            final byte[] body,
            final Manager sessionManager
    ) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.sessionManager = sessionManager;
    }

    public static HttpRequest readFrom(final InputStream inputStream, final Manager sessionManager) throws IOException {
        final BufferedReader reader = getReader(inputStream);
        final List<String> requestHeadLines = readRequestHead(reader);
        validateRequest(requestHeadLines);
        final int separatorIndex = getSeparatorIndex(requestHeadLines);
        final RequestLine requestLine = RequestLine.from(requestHeadLines.getFirst());
        final Map<String, String> headers = parseHeaders(requestHeadLines.subList(1, separatorIndex));
        final byte[] body = readRequestBody(reader, headers);

        return new HttpRequest(requestLine, headers, body, sessionManager);
    }

    public RequestLine requestLine() {
        return requestLine;
    }

    public Map<String, String> headers() {
        return headers;
    }

    public byte[] body() {
        return body.clone();
    }

    public Optional<String> requestedSessionId() {
        return cookie(HttpCookie.JSESSIONID);
    }

    public Session getSession(final boolean create) throws IOException {
        if (session == null) {
            session = findRequestedSession();
        }
        if (session == null && create) {
            session = createSession();
        }
        return session;
    }

    private Session findRequestedSession() throws IOException {
        final Optional<String> sessionId = requestedSessionId();
        if (sessionId.isEmpty()) {
            return null;
        }
        return sessionManager.findSession(sessionId.get());
    }

    private Session createSession() {
        final Session newSession = new Session(UUID.randomUUID().toString());
        sessionManager.add(newSession);
        return newSession;
    }

    public String path() {
        final String requestTarget = requestLine.requestTarget();
        final int queryIndex = requestTarget.indexOf('?');
        if (queryIndex < 0) {
            return requestTarget;
        }
        return requestTarget.substring(0, queryIndex);
    }

    public Optional<String> cookie(final String name) {
        return HttpCookie.from(headers.getOrDefault("cookie", ""))
                .getValue(name);
    }

    public Map<String, String> formParameters() {
        final String formData = new String(body, StandardCharsets.UTF_8);
        if (formData.isBlank()) {
            return Map.of();
        }

        final Map<String, String> parameters = new HashMap<>();
        for (final String parameter : formData.split("&")) {
            final String[] keyAndValue = parameter.split("=", 2);
            validateFormParameter(keyAndValue);
            final String value = URLDecoder.decode(keyAndValue[1], StandardCharsets.UTF_8);
            parameters.put(keyAndValue[0], value);
        }
        return Map.copyOf(parameters);
    }

    private static int parseContentLength(final Map<String, String> headers) {
        final String contentLength = headers.getOrDefault("content-length", "0");
        final int parsedContentLength = Integer.parseInt(contentLength);
        if (parsedContentLength < 0) {
            throw new IllegalArgumentException("Content-Length must not be negative");
        }
        return parsedContentLength;
    }

    private static List<String> readRequestHead(final BufferedReader reader) throws IOException {
        final List<String> requestHeadLines = new ArrayList<>();
        String line;
        while ((line = reader.readLine()) != null) {
            requestHeadLines.add(line);
            if (line.isEmpty()) {
                break;
            }
        }
        return requestHeadLines;
    }

    private static byte[] readRequestBody(final BufferedReader reader, final Map<String, String> headers)
            throws IOException {
        final int contentLength = parseContentLength(headers);
        final char[] buffer = new char[contentLength];
        int offset = 0;

        while (offset < contentLength) {
            final int readCount = reader.read(buffer, offset, contentLength - offset);
            if (readCount < 0) {
                throw new IllegalArgumentException("HTTP request body is shorter than Content-Length");
            }
            offset += readCount;
        }
        return new String(buffer).getBytes(StandardCharsets.UTF_8);
    }

    private static BufferedReader getReader(final InputStream inputStream) {
        return new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
    }

    private static void validateRequest(List<String> requestLines) {
        if (requestLines == null || requestLines.isEmpty()) {
            throw new IllegalArgumentException(
                    "HTTP request must not be empty"
            );
        }
    }

    private static int getSeparatorIndex(final List<String> requestLines) {
        int separatorIndex = requestLines.indexOf(EMPTY_LINE);

        if (separatorIndex < 0) {
            throw new IllegalArgumentException(
                    "HTTP request must contain an empty line"
            );
        }

        return separatorIndex;
    }

    private static Map<String, String> parseHeaders(
            final List<String> headerLines
    ) {
        final Map<String, String> headers =
                new HashMap<>();

        for (final String headerLine : headerLines) {
            final String[] headerParts =
                    headerLine.split(":", 2);

            validateHeaderPart(headerLine, headerParts);
            final String name = headerParts[0]
                    .trim()
                    .toLowerCase(Locale.ROOT);

            final String value = headerParts[1].trim();

            headers.put(name, value);
        }

        return Map.copyOf(headers);
    }

    private static void validateHeaderPart(String headerLine, String[] headerParts) {
        if (headerParts.length != 2) {
            throw new IllegalArgumentException(
                    "Invalid HTTP header: " + headerLine
            );
        }
    }

    private static void validateFormParameter(final String[] keyAndValue) {
        if (keyAndValue.length != 2) {
            throw new IllegalArgumentException("Invalid form parameter");
        }
    }
}
