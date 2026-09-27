package org.apache.coyote.http11;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private final RequestLine requestLine;
    private final Map<String, String> headers;
    private final String body;
    private final Map<String, String> queryParameters;
    private final Map<String, String> bodyParameters;

    private HttpRequest(final RequestLine requestLine, final Map<String, String> headers, final String body) {
        this.requestLine = requestLine;
        this.headers = headers;
        this.body = body;
        this.queryParameters = parseFormData(requestLine.getQueryString());
        this.bodyParameters = parseFormData(body);
    }

    public static HttpRequest from(final BufferedReader reader) throws IOException {
        final RequestLine requestLine = RequestLine.from(reader.readLine());
        final Map<String, String> headers = parseHeaders(reader);
        final String body = readBody(reader, headers);
        return new HttpRequest(requestLine, headers, body);
    }

    private static Map<String, String> parseHeaders(final BufferedReader reader) throws IOException {
        final Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = reader.readLine()) != null) { // 바디 전까지
            if (line.isEmpty()) {
                break; // 빈 줄이면 헤더 끝
            }
            final String[] keyValue = line.split(":", 2);
            if (keyValue.length == 2) {
                headers.put(keyValue[0], keyValue[1].trim());
            }
        }
        return headers;
    }

    private static String readBody(final BufferedReader reader, final Map<String, String> headers)
            throws IOException {
        final String contentLengthHeader = headers.get("Content-Length");
        if (contentLengthHeader == null) { // 바디가 없는 경우
            return "";
        }

        final int contentLength = Integer.parseInt(contentLengthHeader);
        final char[] buffer = new char[contentLength];
        int totalRead = 0;
        while (totalRead < contentLength) {
            final int read = reader.read(buffer, totalRead, contentLength - totalRead);
            if (read == -1) {
                throw new IOException("[ERROR] 요청 본문이 Content-Length보다 짧습니다.");
            }
            totalRead += read;
        }
        return new String(buffer);
    }

    private static Map<String, String> parseFormData(final String rawParameters) {
        final Map<String, String> parameters = new HashMap<>();
        if (rawParameters.isEmpty()) {
            return parameters;
        }

        for (final String pair : rawParameters.split("&")) { // "account=gugu", "password=password"
            final String[] keyValue = pair.split("=", 2); // ["account", "gugu"]
            if (keyValue.length == 2) {
                parameters.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8)); // "account" -> "gugu"
            }
        }
        return parameters;
    }

    public String getMethod() {
        return requestLine.getMethod();
    }

    public String getPath() {
        return requestLine.getPath();
    }

    public String getHeader(final String name) {
        return headers.get(name);
    }

    public String getBody() {
        return body;
    }

    public String getQueryParameter(final String name) {
        return queryParameters.get(name);
    }

    public String getBodyParameter(final String name) {
        return bodyParameters.get(name);
    }
}
