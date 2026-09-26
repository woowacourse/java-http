package org.apache.coyote.http11;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;

public final class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final HttpCookie cookies;
    private final String body;

    private HttpRequest(RequestLine requestLine, Map<String, String> headers, String body) {
        this.requestLine = requestLine;
        this.headers = Map.copyOf(headers);
        this.cookies = new HttpCookie(this.headers.getOrDefault("cookie", ""));
        this.body = body;
    }

    public static HttpRequest readFrom(InputStream inputStream) throws IOException {
        BufferedInputStream input = new BufferedInputStream(inputStream);
        String requestLineValue = readLine(input);

        if (requestLineValue == null) {
            return null;
        }

        RequestLine requestLine = RequestLine.from(requestLineValue);
        Map<String, String> headers = new HashMap<>();
        String headerLine;
        while ((headerLine = readLine(input)) != null && !headerLine.isEmpty()) {
            int colonIndex = headerLine.indexOf(':');
            if (colonIndex > 0) {
                String name = headerLine.substring(0, colonIndex).toLowerCase(Locale.ROOT);
                headers.put(name, headerLine.substring(colonIndex + 1).trim());
            }
        }

        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        byte[] bodyBytes = input.readNBytes(contentLength);
        if (bodyBytes.length < contentLength) {
            throw new EOFException("요청 본문이 Content-Length보다 짧습니다");
        }
        return new HttpRequest(
                requestLine,
                headers,
                new String(bodyBytes, StandardCharsets.UTF_8)
        );
    }

    private static String readLine(InputStream input) throws IOException {
        ByteArrayOutputStream line = new ByteArrayOutputStream();
        int next;
        while ((next = input.read()) != -1 && next != '\n') {
            line.write(next);
        }
        if (next == -1 && line.size() == 0) {
            return null;
        }
        byte[] bytes = line.toByteArray();
        int length = bytes.length;
        if (length > 0 && bytes[length - 1] == '\r') {
            length--;
        }
        return new String(bytes, 0, length, StandardCharsets.ISO_8859_1);
    }

    public boolean matches(
            final String expectedMethod,
            final String expectedPath
    ) {
        return requestLine.method() == HttpMethod.from(expectedMethod)
                && requestLine.target().hasPath(expectedPath);
    }

    public String path() {
        return requestLine.target().path();
    }

    public String extension() {
        return requestLine.target().extension();
    }

    public HttpMethod method() {
        return requestLine.method();
    }

    public Optional<String> findHeader(String name) {
        return Optional.ofNullable(headers.get(name.toLowerCase(Locale.ROOT)));
    }

    public Optional<String> findCookie(String name) {
        return cookies.findValue(name);
    }

    public Optional<String> findFormParameter(String name) {
        for (String parameter : body.split("&")) {
            String[] pair = parameter.split("=", 2);
            if (pair.length == 2 && URLDecoder.decode(pair[0], StandardCharsets.UTF_8).equals(name)) {
                return Optional.of(URLDecoder.decode(pair[1], StandardCharsets.UTF_8));
            }
        }
        return Optional.empty();
    }
}
