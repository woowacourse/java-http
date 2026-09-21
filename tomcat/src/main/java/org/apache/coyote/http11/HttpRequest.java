package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class HttpRequest {

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final HttpCookie cookies;
    private final Map<String, String> bodyParameters;

    public HttpRequest(
            RequestLine requestLine,
            Map<String, String> headers,
            String body
    ) {
        this.requestLine = Objects.requireNonNull(requestLine);
        this.headers = normalizeHeaders(headers);
        this.body = Objects.requireNonNull(body);
        this.cookies = new HttpCookie(getHeader("cookie"));
        this.bodyParameters = parseBodyParameters(body);
    }

    public static HttpRequest parse(InputStream inputStream) throws IOException {
        Objects.requireNonNull(inputStream);
        RequestLine requestLine = RequestLine.parse(readLine(inputStream));
        Map<String, String> headers = readHeaders(inputStream);
        String body = readBody(inputStream, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    private static Map<String, String> normalizeHeaders(Map<String, String> headers) {
        Map<String, String> normalizedHeaders = new HashMap<>();
        Objects.requireNonNull(headers).forEach((name, value) ->
                normalizedHeaders.put(
                        Objects.requireNonNull(name).toLowerCase(Locale.ROOT),
                        Objects.requireNonNull(value)
                )
        );
        return Map.copyOf(normalizedHeaders);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }

    public String getHeader(String name) {
        return headers.get(name.toLowerCase(Locale.ROOT));
    }

    public String getBody() {
        return body;
    }

    public String getParameter(String name) {
        String queryParameter = requestLine.getRequestUri().getQueryParameter(name);
        if (queryParameter != null) {
            return queryParameter;
        }
        return bodyParameters.get(name);
    }

    public Optional<String> getCookie(String name) {
        return cookies.get(name);
    }

    public Optional<String> createJSessionIdIfAbsent() {
        return cookies.createJSessionIdIfAbsent();
    }

    private static Map<String, String> readHeaders(InputStream inputStream) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = readLine(inputStream)) != null && !line.isEmpty()) {
            String[] pair = line.split(":", 2);

            if (pair.length == 2) {
                headers.put(pair[0].trim().toLowerCase(Locale.ROOT), pair[1].trim());
            }
        }

        return headers;
    }

    private static String readBody(
            InputStream inputStream,
            Map<String, String> headers
    ) throws IOException {
        int contentLength = parseContentLength(headers.getOrDefault("content-length", "0"));
        byte[] body = inputStream.readNBytes(contentLength);
        if (body.length != contentLength) {
            throw new HttpRequestParseException("요청 본문이 Content-Length보다 짧습니다.");
        }

        return new String(body, StandardCharsets.UTF_8);
    }

    private static int parseContentLength(String value) {
        try {
            int contentLength = Integer.parseInt(value);
            if (contentLength < 0) {
                throw new HttpRequestParseException("Content-Length는 0 이상이어야 합니다.");
            }
            return contentLength;
        } catch (NumberFormatException e) {
            throw new HttpRequestParseException("잘못된 Content-Length입니다.", e);
        }
    }

    private static String readLine(InputStream inputStream) throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int value;

        while ((value = inputStream.read()) != -1) {
            if (value == '\n') {
                byte[] line = buffer.toByteArray();
                int length = line.length;
                if (length > 0 && line[length - 1] == '\r') {
                    length--;
                }
                return new String(line, 0, length, StandardCharsets.UTF_8);
            }
            buffer.write(value);
        }

        if (buffer.size() == 0) {
            return null;
        }
        return buffer.toString(StandardCharsets.UTF_8);
    }

    private Map<String, String> parseBodyParameters(String body) {
        String contentType = getHeader("content-type");
        if (contentType == null
                || !contentType.startsWith("application/x-www-form-urlencoded")) {
            return Map.of();
        }

        return UrlEncodedParameters.parse(body);
    }
}
