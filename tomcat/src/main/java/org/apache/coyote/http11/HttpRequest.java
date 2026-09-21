package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
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
        this.headers = Map.copyOf(headers);
        this.body = Objects.requireNonNull(body);
        this.cookies = new HttpCookie(getHeader("cookie"));
        this.bodyParameters = parseBodyParameters(body);
    }

    public static HttpRequest parse(BufferedReader reader) throws IOException {
        RequestLine requestLine = RequestLine.parse(reader.readLine());
        Map<String, String> headers = readHeaders(reader);
        String body = readBody(reader, headers);

        return new HttpRequest(requestLine, headers, body);
    }

    public HttpMethod getMethod() {
        return requestLine.getMethod();
    }

    public RequestUri getRequestUri() {
        return requestLine.getRequestUri();
    }

    public String getHeader(String name) {
        return headers.get(name);
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

    private static Map<String, String> readHeaders(BufferedReader reader) throws IOException {
        Map<String, String> headers = new HashMap<>();
        String line;

        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            String[] pair = line.split(":", 2);

            if (pair.length == 2) {
                headers.put(pair[0].trim().toLowerCase(Locale.ROOT), pair[1].trim());
            }
        }

        return headers;
    }

    private static String readBody(
            BufferedReader reader,
            Map<String, String> headers
    ) throws IOException {
        int contentLength = Integer.parseInt(headers.getOrDefault("content-length", "0"));
        char[] buffer = new char[contentLength];
        int totalRead = 0;

        while (totalRead < contentLength) {
            int readCount = reader.read(
                    buffer,
                    totalRead,
                    contentLength - totalRead
            );

            if (readCount == -1) {
                throw new IOException("요청 본문이 Content-Length보다 짧습니다.");
            }

            totalRead += readCount;
        }

        return new String(buffer);
    }

    private Map<String, String> parseBodyParameters(String body) {
        String contentType = getHeader("content-type");
        if (contentType == null
                || !contentType.startsWith("application/x-www-form-urlencoded")) {
            return Map.of();
        }

        Map<String, String> parameters = new HashMap<>();

        for (String parameter : body.split("&")) {
            String[] pair = parameter.split("=", 2);
            if (pair.length != 2) {
                continue;
            }

            String name = URLDecoder.decode(pair[0], StandardCharsets.UTF_8);
            String value = URLDecoder.decode(pair[1], StandardCharsets.UTF_8);
            parameters.put(name, value);
        }
        return Map.copyOf(parameters);
    }
}
