package org.apache.coyote.http11;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class HttpRequest {

    private static final String CONTENT_LENGTH = "Content-Length";

    private static final String COOKIE = "Cookie";

    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> parameters;
    private final HttpCookie cookies;

    private HttpRequest(final RequestLine requestLine,
                        final Map<String, String> headers, final String body) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.body = body;
        this.parameters = Map.copyOf(parseParameters(requestLine, body));
        this.cookies = HttpCookie.from(headers.get(COOKIE));
    }

    public static Optional<HttpRequest> from(
            final InputStream inputStream
    ) throws IOException {

        final String rawRequestLine = readLine(inputStream);

        if (rawRequestLine == null) {
            return Optional.empty();
        }

        final RequestLine requestLine = RequestLine.from(rawRequestLine);

        final Map<String, String> headers = readHeaders(inputStream);

        final String body = readBody(inputStream, headers);

        return Optional.of(new HttpRequest(requestLine, headers, body));
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getUri() {
        return requestLine.getUri();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public Optional<String> getHeader(final String name) {
        return Optional.ofNullable(headers.get(name));
    }

    public String getBody() {
        return body;
    }

    public Optional<String> getParameter(final String name) {
        return Optional.ofNullable(parameters.get(name));
    }

    public Optional<String> getCookie(final String name) {
        return cookies.get(name);
    }

    private static String readLine(final InputStream inputStream
    ) throws IOException {

        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();

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

    private static Map<String, String> readHeaders(
            final InputStream inputStream
    ) throws IOException {

        final Map<String, String> headers = new HashMap<>();

        String line;
        while ((line = readLine(inputStream)) != null) {
            if (line.isEmpty()) {  // Header와 Body 사이의 빈 줄
                break;
            }

            final int colonIndex = line.indexOf(":");

            if (colonIndex == -1) {
                continue;
            }

            final String name = line.substring(0, colonIndex).trim();
            final String value = line.substring(colonIndex + 1).trim();
            headers.put(name, value);
        }

        return headers;
    }

    private static String readBody(
            final InputStream inputStream,
            final Map<String, String> headers
    ) throws IOException {

        final String contentLengthValue = headers.get(CONTENT_LENGTH);// 바이트 수

        if (contentLengthValue == null) {
            return "";
        }

        final int contentLength;

        try {
            contentLength = Integer.parseInt(contentLengthValue);
        } catch (NumberFormatException e) {
            return "";
        }

        if (contentLength <= 0) {
            return "";
        }

        final byte[] body = inputStream.readNBytes(contentLength);

        return new String(body, StandardCharsets.UTF_8);
    }

    private static Map<String, String> parseParameters(final RequestLine requestLine, final String body) {
        final Map<String, String> parameters = new HashMap<>();
        parameters.putAll(parseParameters(requestLine.getQueryString()));
        parameters.putAll(parseParameters(body));

        return parameters;
    }

    private static Map<String, String> parseParameters(
            final String parameterString
    ) {
        final Map<String, String> parameters = new HashMap<>();

        if (parameterString == null || parameterString.isBlank()) {
            return parameters;
        }

        for (String parameter : parameterString.split("&")) {
            final String[] pair = parameter.split("=", 2);

            if (pair.length != 2) {
                continue;
            }

            parameters.put(decode(pair[0]), decode(pair[1]));
        }

        return parameters;
    }

    private static String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}