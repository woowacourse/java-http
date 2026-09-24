package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

public class HttpRequest {

    private final RequestLine requestLine;
    private final Headers headers;
    private final String body;
    private final Map<String, String> parameters;

    public HttpRequest(final InputStream inputStream) {
        try {
            final BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream, StandardCharsets.UTF_8)
            );
            this.requestLine = new RequestLine(readRequestLine(reader));
            this.headers = readHeaders(reader);
            this.body = readBody(reader);
            this.parameters = Collections.unmodifiableMap(createParameters());
        } catch (IOException e) {
            throw new UncheckedIOException("HTTP 요청을 읽을 수 없습니다.", e);
        }
    }

    public RequestLine getRequestLine() {
        return requestLine;
    }

    public Method getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getProtocolVersion() {
        return requestLine.getProtocolVersion();
    }

    public Headers getHeaders() {
        return headers;
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public String getParameter(final String name) {
        return parameters.get(name);
    }

    private String readRequestLine(final BufferedReader reader) throws IOException {
        final String line = reader.readLine();
        if (line == null) {
            throw new IllegalArgumentException("HTTP 요청이 비어 있습니다.");
        }
        return line;
    }

    private Headers readHeaders(final BufferedReader reader) throws IOException {
        final Headers result = new Headers();
        String line;
        while ((line = reader.readLine()) != null && !line.isEmpty()) {
            final int separator = line.indexOf(':');
            if (separator <= 0) {
                throw new IllegalArgumentException("잘못된 헤더입니다: " + line);
            }
            result.put(line.substring(0, separator), line.substring(separator + 1));
        }
        return result;
    }

    private String readBody(final BufferedReader reader) throws IOException {
        final String contentLength = headers.get("Content-Length");
        if (contentLength == null || contentLength.isBlank()) {
            return "";
        }

        final int length;
        try {
            length = Integer.parseInt(contentLength);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Content-Length가 올바르지 않습니다: " + contentLength, e);
        }
        if (length < 0) {
            throw new IllegalArgumentException("Content-Length는 음수일 수 없습니다.");
        }

        final char[] content = new char[length];
        int offset = 0;
        while (offset < length) {
            final int read = reader.read(content, offset, length - offset);
            if (read == -1) {
                throw new IOException("Content-Length만큼 요청 바디를 읽지 못했습니다.");
            }
            offset += read;
        }
        return new String(content);
    }

    private Map<String, String> createParameters() {
        final Map<String, String> result = new LinkedHashMap<>(requestLine.getQueryParameters());
        result.putAll(parseQueryString(body));
        return result;
    }

    private Map<String, String> parseQueryString(final String queryString) {
        if (queryString == null || queryString.isBlank()) {
            return Map.of();
        }

        final Map<String, String> result = new LinkedHashMap<>();
        for (String parameter : queryString.split("&")) {
            final String[] keyValue = parameter.split("=", 2);
            if (keyValue.length != 2) {
                continue;
            }
            result.put(decode(keyValue[0]), decode(keyValue[1]));
        }
        return result;
    }

    private String decode(final String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
