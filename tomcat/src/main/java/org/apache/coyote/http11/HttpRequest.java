package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final Map<String, String> parameters;
    private final Map<String, String> cookies;
    private Http11Processor.Session session;
    private boolean newSession;

    public HttpRequest(final InputStream inputStream) throws IOException {
        final String firstLine = readLine(inputStream);
        if (firstLine == null) {
            throw new IllegalArgumentException("HTTP 요청 라인이 없습니다.");
        }
        requestLine = new RequestLine(firstLine);
        headers = readHeaders(inputStream);
        parameters = parseParameters(requestLine.getRequestTarget());
        if ("POST".equals(requestLine.getMethod())) {
            parameters.putAll(parseFormBody(inputStream));
        }
        cookies = parseCookies(headers.get("cookie"));
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocol() {
        return requestLine.getProtocol();
    }

    public String getHeader(final String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    public Map<String, String> getParameters() {
        return Map.copyOf(parameters);
    }

    public String getCookie(final String name) {
        return cookies.get(name);
    }

    public Http11Processor.Session getSession() {
        return session;
    }

    void setSession(final Http11Processor.Session session, final boolean newSession) {
        this.session = session;
        this.newSession = newSession;
    }

    boolean isNewSession() {
        return newSession;
    }

    private String readLine(final InputStream inputStream) throws IOException {
        final var buffer = new ByteArrayOutputStream();
        int value;
        while ((value = inputStream.read()) != -1) {
            if (value == '\n') {
                break;
            }
            if (value != '\r') {
                buffer.write(value);
            }
        }
        if (value == -1 && buffer.size() == 0) {
            return null;
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private Map<String, String> readHeaders(final InputStream inputStream) throws IOException {
        final Map<String, String> result = new HashMap<>();
        String line;
        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            final String[] header = line.split(":", 2);
            if (header.length == 2) {
                result.put(header[0].trim().toLowerCase(Locale.ROOT), header[1].trim());
            }
        }
        return result;
    }

    private Map<String, String> parseFormBody(final InputStream inputStream) throws IOException {
        final String contentType = headers.getOrDefault("content-type", "");
        if (!contentType.toLowerCase(Locale.ROOT).startsWith("application/x-www-form-urlencoded")) {
            return Map.of();
        }
        final int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        return parseQueryString(new String(inputStream.readNBytes(contentLength), StandardCharsets.UTF_8));
    }

    private Map<String, String> parseParameters(final String requestTarget) {
        final String[] targetParts = requestTarget.split("\\?", 2);
        return parseQueryString(targetParts.length == 2 ? targetParts[1] : "");
    }

    private Map<String, String> parseQueryString(final String queryString) {
        final Map<String, String> result = new HashMap<>();
        if (queryString.isBlank()) {
            return result;
        }
        for (final String parameter : queryString.split("&")) {
            final String[] pair = parameter.split("=", 2);
            if (pair.length == 2) {
                result.put(URLDecoder.decode(pair[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return result;
    }

    private Map<String, String> parseCookies(final String cookieHeader) {
        final Map<String, String> result = new HashMap<>();
        if (cookieHeader == null || cookieHeader.isBlank()) {
            return result;
        }
        for (final String cookie : cookieHeader.split(";")) {
            final String[] pair = cookie.trim().split("=", 2);
            if (pair.length == 2) {
                result.put(pair[0].trim(), pair[1].trim());
            }
        }
        return result;
    }
}
